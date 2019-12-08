package ru.bobko.shop.core.model;

import redis.clients.jedis.Jedis;
import ru.bobko.shop.core.model.good.Good;

import java.util.Map;

public class BackendUserCart implements UserCart {
  private final Jedis jedis;
  private String clientId;

  public BackendUserCart(Jedis jedis, String clientId) {
    this.jedis = jedis;
    this.clientId = clientId;
  }

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public boolean add(Good good) {
    return false;
  }

  @Override
  public boolean removeFromCart(Good good) {
    return false;
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
    return null;
  }
}
