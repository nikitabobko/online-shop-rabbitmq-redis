package ru.bobko.shop.backend.model;

import redis.clients.jedis.Jedis;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;

import java.util.Map;
import java.util.Set;

public class BackendWarehouse implements Warehouse {
  private final Jedis jedis;

  private final static String VENDOR_CODE_TO_NAME_REDIS_KEY = "vendor_code_to_name";
  private final static String VENDOR_CODE_TO_PRICE_REDIS_KEY = "vendor_code_to_price";
  private final static String VENDOR_CODE_TO_CATEGORIES_SET_KEY_REDIS_KEY = "vendor_code_to_price";

  public BackendWarehouse(Jedis jedis) {
    this.jedis = jedis;
  }

  @Override
  public Good getGoodByVendorCodeNullable(String vendorCode) {
    String name = jedis.hget(VENDOR_CODE_TO_NAME_REDIS_KEY, vendorCode);
    String priceString = jedis.hget(VENDOR_CODE_TO_PRICE_REDIS_KEY, vendorCode);
    String setKey = jedis.hget(VENDOR_CODE_TO_CATEGORIES_SET_KEY_REDIS_KEY, vendorCode);
    if (name == null || priceString == null || setKey == null) {
      return null;
    }
    Set<String> categories = jedis.smembers(setKey);
    if (categories == null) {
      return null;
    }
    int price = Integer.parseInt(priceString);
    return new Good(vendorCode, name, price, categories);
  }

  @Override
  public Map<Good, Integer> showAll() {
    return null;
  }
}
