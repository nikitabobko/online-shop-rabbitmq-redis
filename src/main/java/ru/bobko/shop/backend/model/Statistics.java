package ru.bobko.shop.backend.model;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;

import java.util.Map;
import java.util.Optional;

public class Statistics {
  private static final String CART_COUNT_REDIS_KEY = "cart_count";
  private static final String CART_PRICE_REDIS_KEY = "cart_price";

  private static final String USER_COUNT_REDIS_KEY = "user_count";

  private static final String USERS_THAT_BOUGHT_SMTH_REDIS_KEY = "users_that_bought_smth";

  private static final String GOODS_COUNT_REDIS_KEY = "goods_count";
  private static final String PURCHASES_COUNT_REDIS_KEY = "purchases_count";

  private final JedisPool pool;
  private final Warehouse warehouse;

  public Statistics(JedisPool pool, Warehouse warehouse) {
    this.pool = pool;
    this.warehouse = warehouse;
  }

  public double averageCartPrice(Mode mode) {
    try (Jedis jedis = pool.getResource()) {
      int count;
      int price;
      if (mode == Mode.BOTH) {
        count = Optional.ofNullable(jedis.get(CART_COUNT_REDIS_KEY + ":" + Mode.WHO_BOUGHT)).map(Integer::parseInt).orElse(0);
        price = Optional.ofNullable(jedis.get(CART_PRICE_REDIS_KEY + ":" + Mode.WHO_BOUGHT)).map(Integer::parseInt).orElse(0);

        count += Optional.ofNullable(jedis.get(CART_COUNT_REDIS_KEY + ":" + Mode.WHO_JUST_CAME_INTO_SHOP)).map(Integer::parseInt).orElse(0);
        price += Optional.ofNullable(jedis.get(CART_PRICE_REDIS_KEY + ":" + Mode.WHO_JUST_CAME_INTO_SHOP)).map(Integer::parseInt).orElse(0);
      } else {
        count = Optional.ofNullable(jedis.get(CART_COUNT_REDIS_KEY + ":" + mode)).map(Integer::parseInt).orElse(0);
        price = Optional.ofNullable(jedis.get(CART_PRICE_REDIS_KEY + ":" + mode)).map(Integer::parseInt).orElse(0);
      }
      if (count == 0) {
        return 0;
      }
      return 1.0 * price / count;
    }
  }

  public void reportNewBuy(UserCart user, Map<Good, Integer> inCart) {
    try (Jedis jedis = pool.getResource()) {
      int cartPrice = inCart.entrySet().stream()
        .map(it -> it.getKey().price * it.getValue())
        .reduce(Integer::sum)
        .orElse(0);
      Integer count = inCart.values().stream().reduce(Integer::sum).orElse(0);
      jedis.set(CART_COUNT_REDIS_KEY + ":" + Mode.WHO_BOUGHT, String.valueOf(count));
      jedis.set(CART_PRICE_REDIS_KEY + ":" + Mode.WHO_BOUGHT, String.valueOf(cartPrice));

      jedis.sadd(USERS_THAT_BOUGHT_SMTH_REDIS_KEY, user.getClientId());

      jedis.incr(PURCHASES_COUNT_REDIS_KEY);
      int goodsCount = Optional.ofNullable(jedis.get(GOODS_COUNT_REDIS_KEY)).map(Integer::parseInt).orElse(0);
      jedis.set(GOODS_COUNT_REDIS_KEY, String.valueOf(goodsCount + warehouse.getAll().keySet().size()));
    }
  }

  public void reportUserRemoved(UserCart cart) {
    try (Jedis jedis = pool.getResource()) {
      if (jedis.sismember(USERS_THAT_BOUGHT_SMTH_REDIS_KEY, cart.getClientId())) {
        jedis.incr(USER_COUNT_REDIS_KEY + ":" + Mode.WHO_BOUGHT);

        jedis.srem(USERS_THAT_BOUGHT_SMTH_REDIS_KEY, cart.getClientId());
      } else {
        jedis.incr(USER_COUNT_REDIS_KEY + ":" + Mode.WHO_JUST_CAME_INTO_SHOP);
      }
    }
  }

  public int countOfClients(Mode mode) {
    try (Jedis jedis = pool.getResource()) {
      if (mode == Mode.BOTH) {
        return Optional.ofNullable(jedis.get(USER_COUNT_REDIS_KEY + ":" + Mode.WHO_BOUGHT)).map(Integer::parseInt).orElse(0) +
          Optional.ofNullable(jedis.get(USER_COUNT_REDIS_KEY + ":" + Mode.WHO_JUST_CAME_INTO_SHOP)).map(Integer::parseInt).orElse(0);
      }
      return Optional.ofNullable(jedis.get(USER_COUNT_REDIS_KEY + ":" + mode)).map(Integer::parseInt).orElse(0);
    }
  }

  public double averageCountOfDifferentGoods() {
    try (Jedis jedis = pool.getResource()) {
      int goodsCount = Optional.ofNullable(jedis.get(GOODS_COUNT_REDIS_KEY)).map(Integer::parseInt).orElse(0);
      int purchasesCount = Optional.ofNullable(jedis.get(PURCHASES_COUNT_REDIS_KEY)).map(Integer::parseInt).orElse(0);

      if (purchasesCount == 0) {
        return 0;
      }

      return 1.0 * goodsCount / purchasesCount;
    }
  }

  public enum Mode {
    WHO_BOUGHT, WHO_JUST_CAME_INTO_SHOP, BOTH
  }
}
