package ru.bobko.shop.backend.model;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class BackendWarehouse implements Warehouse {
  private final JedisPool pool;

  private final static String VENDOR_CODE_TO_NAME_REDIS_KEY = "vendor_code_to_name";
  private final static String VENDOR_CODE_TO_PRICE_REDIS_KEY = "vendor_code_to_price";
  private final static String CATEGORIES_KEY_REDIS_KEY = "good_categories";

  private final static String VENDOR_CODE_TO_AMOUNT = "vendor_code_to_amount";

  public BackendWarehouse(JedisPool pool) {
    this.pool = pool;
  }

  @Override
  public Good getGoodByVendorCodeNullable(String vendorCode) {
    try (Jedis jedis = pool.getResource()) {
      String name = jedis.hget(VENDOR_CODE_TO_NAME_REDIS_KEY, vendorCode);
      String priceString = jedis.hget(VENDOR_CODE_TO_PRICE_REDIS_KEY, vendorCode);
      Set<String> categories = jedis.smembers(CATEGORIES_KEY_REDIS_KEY + ":" + vendorCode);
      if (name == null || priceString == null || categories == null) {
        return null;
      }
      int price = Integer.parseInt(priceString);
      return new Good(vendorCode, name, price, categories);
    }
  }

  @Override
  public Map<Good, Integer> getAll() {
    try (Jedis jedis = pool.getResource()) {
      Set<String> members = jedis.hkeys(VENDOR_CODE_TO_NAME_REDIS_KEY);
      if (members == null) {
        return Collections.emptyMap();
      }
      List<Good> goods = members.stream().map(this::getGoodByVendorCodeNullable).collect(toList());
      assert goods.stream().allMatch(Objects::nonNull);
      return goods.stream().collect(Collectors.toMap(Function.identity(), this::amountOf));
    }
  }

  @Override
  public int amountOf(Good good) {
    try (Jedis jedis = pool.getResource()) {
      String amountString = jedis.hget(VENDOR_CODE_TO_AMOUNT, good.vendorCode);
      if (amountString == null) {
        throw new IllegalArgumentException("good isn't valid");
      }
      return Integer.parseInt(amountString);
    }
  }

  @Override
  public void setAmountOf(Good good, int amount) {
    assert amount >= 0;
    try (Jedis jedis = pool.getResource()) {
      jedis.hset(VENDOR_CODE_TO_AMOUNT, good.vendorCode, String.valueOf(amount));
    }
  }

  @Override
  public void addGood(Good good, int count) {
    try (Jedis jedis = pool.getResource()) {
      jedis.hset(VENDOR_CODE_TO_NAME_REDIS_KEY, good.vendorCode, good.name);
      jedis.hset(VENDOR_CODE_TO_PRICE_REDIS_KEY, good.vendorCode, String.valueOf(good.price));
      jedis.sadd(CATEGORIES_KEY_REDIS_KEY + ":" + good.vendorCode, good.categories.toArray(new String[0]));

      jedis.hset(VENDOR_CODE_TO_AMOUNT, good.vendorCode, String.valueOf(count));
    }
  }

  @Override
  public void clearGood(Good good) {
    try (Jedis jedis = pool.getResource()) {
      jedis.hdel(VENDOR_CODE_TO_NAME_REDIS_KEY, good.vendorCode);
      jedis.hdel(VENDOR_CODE_TO_PRICE_REDIS_KEY, good.vendorCode);
      jedis.del(CATEGORIES_KEY_REDIS_KEY + ":" + good.vendorCode);

      jedis.hdel(VENDOR_CODE_TO_AMOUNT, good.vendorCode);
    }
  }

  @Override
  public Set<String> getCategories() {
    return getAll().keySet().stream().flatMap(it -> it.categories.stream()).collect(toSet());
  }

  @Override
  public Set<Good> showCategory(String categoryName) {
    return getAll().keySet().stream().filter(it -> it.categories.contains(categoryName)).collect(toSet());
  }
}
