package ru.bobko.shop.frontend.model;

import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;
import ru.bobko.shop.frontend.cli.base.CliException;
import ru.bobko.shop.util.GsonUtil;

import java.util.Map;
import java.util.Objects;

import static ru.bobko.shop.util.FrontendModelUtil.reactServerDoesntRespondOnInterruptedException;

public class FrontendUserCart implements UserCart {
  private final RequestResponseCycleManager manager;
  private final String clientId;

  public FrontendUserCart(RequestResponseCycleManager manager, String clientId) {
    this.manager = Objects.requireNonNull(manager);
    this.clientId = Objects.requireNonNull(clientId);
  }

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public boolean add(Good good) {
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.ADD_TO_CART, getClientId(), good.vendorCode);
      Message response = manager.requestResponseCycle(request);
      return response.status == Message.Status.OK;
    });
  }

  @Override
  public boolean removeFromCart(Good good) {
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.REM_FROM_CART, getClientId(), good.vendorCode);
      Message response = manager.requestResponseCycle(request);
      return response.status == Message.Status.OK;
    });
  }

  @Override
  public Map<Good, Integer> buy() {
    return null;
  }

  @Override
  public void discard() {

  }

  @Override
  public Map<Good, Integer> getCurrentGoodsInCart() {
    return reactServerDoesntRespondOnInterruptedException(() -> {
      Message request = Message.newRequest(Message.Type.SHOW_CART, getClientId(), null);
      Message response = manager.requestResponseCycle(request);
      if (response.status != Message.Status.OK) {
        throw new CliException(response.additionalMsgNullable);
      }
      return GsonUtil.decodeGoodsMap(response.additionalMsgNullable);
    });
  }
}
