package ru.bobko.shop.core.model.message;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import ru.bobko.shop.backend.model.Statistics;
import ru.bobko.shop.backend.model.BackendUsers;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.util.GsonUtil;
import ru.bobko.shop.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toSet;

/**
 * Thread safe
 */
public final class Message {
  /**
   * Session id is unique only for client for regular request
   * <p>
   * And unique for backend for heart beat
   */
  public final int sessionId;
  public final Type type;
  public final String clientId;
  public final Status status;
  public final String additionalMsgNullable;

  private static final AtomicInteger uniqueSessionId = new AtomicInteger();
  private static final String EXCHANGE_NAME = "";
  public static final String TO_BACKEND_ROUTING_KEY = "backend";

  private Message(int sessionId, Type type, String clientId, Status status, String additionalMsgNullable) {
    this.sessionId = sessionId;
    this.type = type;
    this.clientId = clientId;
    this.status = status;
    this.additionalMsgNullable = additionalMsgNullable;
  }

  public Message respondOk() {
    return respondOk(null);
  }

  public Message respondOk(String additionalMsgNullable) {
    return new Message(sessionId, type, clientId, Status.OK, additionalMsgNullable);
  }

  public Message respondNo(String additionalMsgNullable) {
    return new Message(sessionId, type, clientId, Status.NO, additionalMsgNullable);
  }

  public static Message newRequest(Type type, String clientId, String additionalMsgNullable) {
    return new Message(uniqueSessionId.incrementAndGet(), type, clientId, Status.REQUEST, additionalMsgNullable);
  }

  public static Message newHearthBeatRequest(String clientId) {
    return new Message(uniqueSessionId.incrementAndGet(), Type.HEARTH_BEAT, clientId, Status.REQUEST, null);
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public static Message fromJson(String json) {
    return new Gson().fromJson(json, Message.class);
  }

  public void sendToBackend(Channel rabbitMqChannel) throws IOException {
    sendTo(rabbitMqChannel, TO_BACKEND_ROUTING_KEY);
  }

  public void sendTo(Channel rabbitMqChannel, String receiver) throws IOException {
    String json = toJson();
    if (InjectorHolder.getInjector().isDebug()) {
      System.out.println("Sending " + json);
    }
    rabbitMqChannel.basicPublish(EXCHANGE_NAME, receiver, null, json.getBytes());
  }

  public enum Type {
    ADD_TO_CART {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        UserCart cart = users.getOrRegisterUserById(request.clientId);
        Good good = warehouse.getGoodByVendorCodeNullable(request.additionalMsgNullable);
        if (good == null) {
          request.respondNo("Unknown vendor_code").sendTo(channel, request.clientId);
          return;
        }
        Message response = cart.add(good) ? request.respondOk() : request.respondNo("Cannot add good to cart");
        response.sendTo(channel, request.clientId);
      }
    }, REM_FROM_CART {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        UserCart cart = users.getOrRegisterUserById(request.clientId);
        Good good = warehouse.getGoodByVendorCodeNullable(request.additionalMsgNullable);
        if (good == null) {
          request.respondNo("Unknown vendor_code").sendTo(channel, request.clientId);
          return;
        }
        Message response = cart.removeFromCart(good) ? request.respondOk() : request.respondNo("Cannot remove good from cart");
        response.sendTo(channel, request.clientId);
      }
    }, HEARTH_BEAT {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        throw new IllegalStateException("Hearth beat must be processed by separate thread pool");
      }
    }, GET_GOOD_BY_VENDOR_CODE {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        Good good = warehouse.getGoodByVendorCodeNullable(request.additionalMsgNullable);
        if (good == null) {
          request.respondNo("Unknown vendor_code").sendTo(channel, request.clientId);
          return;
        }
        request.respondOk(good.toJson()).sendTo(channel, request.clientId);
      }
    }, SHOW_ALL {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        Map<Good, Integer> all = warehouse.getAll();
        request.respondOk(GsonUtil.encodeGoodsMap(all)).sendTo(channel, request.clientId);
      }
    }, SHOW_CART {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        UserCart userCart = users.getOrRegisterUserById(request.clientId);
        request.respondOk(GsonUtil.encodeGoodsMap(userCart.getCurrentGoodsInCart())).sendTo(channel, request.clientId);
      }
    }, BUY {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        UserCart user = users.getOrRegisterUserById(request.clientId);
        Map<Good, Integer> inCart = user.buy();
        request.respondOk(GsonUtil.encodeGoodsMap(inCart)).sendTo(channel, request.clientId);

        Injector injector = InjectorHolder.getInjector();
        Statistics statistics = injector.getStatistics();
        statistics.reportNewBuy(user, inCart);
      }
    }, DISCARD {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        UserCart cart = users.getOrRegisterUserById(request.clientId);
        cart.discard();
        request.respondOk().sendTo(channel, request.clientId);
      }
    }, LIST_CATEGORIES {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        request.respondOk(new Gson().toJson(warehouse.getCategories())).sendTo(channel, request.clientId);
      }
    }, SHOW_CATEGORY {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        Gson gson = new Gson();
        Set<String> selectedCategory = warehouse.showCategory(request.additionalMsgNullable).stream()
          .map(gson::toJson)
          .collect(toSet());
        request.respondOk(gson.toJson(selectedCategory)).sendTo(channel, request.clientId);
      }
    }, STATISTICS {
      @Override
      public void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException {
        Injector injector = InjectorHolder.getInjector();
        Statistics statistics = injector.getStatistics();
        Map<String, Double> stat = new HashMap<>();
        for (Statistics.Mode mode : Statistics.Mode.values()) {
          stat.put("Average cart price " + mode.name().toLowerCase(), statistics.averageCartPrice(mode));
          stat.put("Count of clients " + mode.name().toLowerCase(), (double) statistics.countOfClients(mode));
        }
        stat.put("Average count of different goods", statistics.averageCountOfDifferentGoods());
        request.respondOk(new Gson().toJson(stat)).sendTo(channel, request.clientId);
      }
    };

    public abstract void backendProcessRequest(Message request, Channel channel, BackendUsers users, Warehouse warehouse) throws IOException;
  }

  public enum Status {
    OK, NO, REQUEST
  }
}
