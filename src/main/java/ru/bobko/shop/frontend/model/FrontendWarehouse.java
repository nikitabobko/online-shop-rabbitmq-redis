package ru.bobko.shop.frontend.model;

import com.google.gson.Gson;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;
import ru.bobko.shop.frontend.cli.base.CliException;
import ru.bobko.shop.util.InterruptableSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
      if (response.status == Message.Status.NO) {
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
      if (response.status == Message.Status.NO) {
        throw new CliException(response.additionalMsgNullable);
      }
      Gson gson = new Gson();
      @SuppressWarnings("unchecked")
      Map<Good, Integer> map = gson.fromJson(Objects.requireNonNull(response.additionalMsgNullable), Map.class);
      return map;
    });
  }

  @Override
  public int amountOf(Good good) {
    throw new IllegalStateException("Not implemented");
  }

  private static <T> T reactServerDoesntRespondOnInterruptedException(InterruptableSupplier<T> supplier) {
    try {
      return supplier.get();
    } catch (InterruptedException e) {
      throw new CliException.ServerDoesntRespondCliException();
    }
  }
}
