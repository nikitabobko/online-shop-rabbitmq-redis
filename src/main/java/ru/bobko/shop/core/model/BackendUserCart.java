package ru.bobko.shop.core.model;

import redis.clients.jedis.Jedis;
import ru.bobko.shop.core.model.good.Good;

import java.util.Collections;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class BackendUserCart implements UserCart {
  private final Jedis jedis;
  private final String clientId;
  private final Warehouse warehouse;

  private static String CURRENT_GOODS_IN_CART_REDIS_KEY = "client_id_to_vendor_codes";

  public BackendUserCart(Jedis jedis, String clientId, Warehouse warehouse) {
    this.jedis = jedis;
    this.clientId = clientId;
    this.warehouse = warehouse;
  }

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public boolean add(Good good) {
    int amount = warehouse.amountOf(good);
    if (amount <= 0) {
      return false;
    }
    warehouse.setAmountOf(good, amount - 1);
    String hget = jedis.hget(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId, good.vendorCode);
    int currentAmount = hget != null ? Integer.parseInt(hget) : 0;
    jedis.hset(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId, good.vendorCode, String.valueOf(currentAmount + 1));
    return true;
  }

  @Override
  public boolean removeFromCart(Good good) {
    int amount = warehouse.amountOf(good);
    warehouse.setAmountOf(good, amount + 1);
    String hget = jedis.hget(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId, good.vendorCode);
    int currentAmount = hget != null ? Integer.parseInt(hget) : 0;
    if (currentAmount == 1) {
      jedis.del(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId);
    } else {
      jedis.hset(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId, good.vendorCode, String.valueOf(currentAmount - 1));
    }
    return true;
  }

  @Override
  public Map<Good, Integer> buy() {
    Map<Good, Integer> goods = getCurrentGoodsInCart();
    for (Good good : goods.keySet()) {
      int amount = warehouse.amountOf(good);
      if (amount == 0) {
        warehouse.clearGood(good);
      }
    }
    jedis.del(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId);
    return goods;
  }

  @Override
  public void discard() {
    Map<Good, Integer> currentGoodsInCart = getCurrentGoodsInCart();
    for (Map.Entry<Good, Integer> goodAndAmountEntry : currentGoodsInCart.entrySet()) {
      int curAmount = warehouse.amountOf(goodAndAmountEntry.getKey());
      warehouse.setAmountOf(goodAndAmountEntry.getKey(), curAmount + goodAndAmountEntry.getValue());
    }
    jedis.del(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId);
  }

  @Override
  public Map<Good, Integer> getCurrentGoodsInCart() {
    Map<String, String> fromRedis = jedis.hgetAll(CURRENT_GOODS_IN_CART_REDIS_KEY + ":" + clientId);
    if (fromRedis == null) {
      return Collections.emptyMap();
    }
    return fromRedis.entrySet().stream()
      .collect(toMap(it -> warehouse.getGoodByVendorCodeNullable(it.getKey()), it -> Integer.parseInt(it.getValue())));
  }
}
