package ru.bobko.shop.frontend.model;

import com.google.gson.Gson;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;
import ru.bobko.shop.frontend.cli.base.CliException;
import ru.bobko.shop.util.GsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static ru.bobko.shop.util.FrontendModelUtil.reactServerDoesntRespondOnInterruptedException;

public class FrontendWarehouse implements Warehouse {
  private final RequestResponseCycleManager manager;
  private final String clientId;
  private final Map<String, Good> vendorCodeToGoodCache = new HashMap<>();

  public FrontendWarehouse(RequestResponseCycleManager manager, String clientId) {
    this.manager = manager;
    this.clientId = clientId;
  }

  @Override
  public Good getGoodByVendorCodeNullable(String vendorCode) {
    Good cached = vendorCodeToGoodCache.get(vendorCode);
    if (cached != null) {
      return cached;
    }
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.GET_GOOD_BY_VENDOR_CODE, clientId, vendorCode);
      Message response = manager.requestResponseCycle(request);
      if (response.status != Message.Status.OK) {
        throw new CliException(response.additionalMsgNullable);
      }
      Objects.requireNonNull(response.additionalMsgNullable);
      Good good = Good.fromJson(response.additionalMsgNullable);
      vendorCodeToGoodCache.put(vendorCode, good);
      return good;
    });
  }

  @Override
  public Map<Good, Integer> getAll() {
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.SHOW_ALL, clientId, null);
      Message response = manager.requestResponseCycle(request);
      if (response.status != Message.Status.OK) {
        throw new CliException(response.additionalMsgNullable);
      }
      return GsonUtil.decodeGoodsMap(response.additionalMsgNullable);
    });
  }

  @Override
  public int amountOf(Good good) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void setAmountOf(Good good, int amount) {
    throw new IllegalStateException("Not allowed for frontend");
  }

  @Override
  public void addGood(Good good, int count) {
    throw new IllegalStateException("Not allowed for frontend");
  }

  @Override
  public void clearGood(Good good) {
    throw new IllegalStateException("Not allowed for frontend");
  }

  @Override
  public Set<String> getCategories() {
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.LIST_CATEGORIES, clientId, null);
      Message response = manager.requestResponseCycle(request);
      if (response.status != Message.Status.OK) {
        throw new CliException(response.additionalMsgNullable);
      }
      return new Gson().<Set<String>>fromJson(response.additionalMsgNullable, Set.class);
    });
  }

  @Override
  public Set<Good> showCategory(String categoryName) {
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.SHOW_CATEGORY, clientId, categoryName);
      Message response = manager.requestResponseCycle(request);
      if (response.status != Message.Status.OK) {
        throw new CliException(response.additionalMsgNullable);
      }
      Gson gson = new Gson();
      return gson.<Set<String>>fromJson(response.additionalMsgNullable, Set.class).stream()
        .map(it -> gson.fromJson(it, Good.class))
        .collect(toSet());
    });
  }
}
