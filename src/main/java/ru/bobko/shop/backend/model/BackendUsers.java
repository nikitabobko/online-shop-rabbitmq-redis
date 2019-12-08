package ru.bobko.shop.backend.model;

import redis.clients.jedis.Jedis;
import ru.bobko.shop.core.model.BackendUserCart;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BackendUsers {
  private final Jedis jedis;
  private final static String CLIENT_IDS_REDIS_KEY = "client_ids";
  private final Warehouse warehouse;

  public BackendUsers(Jedis jedis, Warehouse warehouse) {
    this.jedis = jedis;
    this.warehouse = warehouse;
  }

  public Set<UserCart> getAllUsers() {
    return jedis.smembers(CLIENT_IDS_REDIS_KEY).stream().map(it -> new BackendUserCart(jedis, it, warehouse)).collect(toSet());
  }

  public void registerUser(UserCart cart) {
    jedis.sadd(CLIENT_IDS_REDIS_KEY, cart.getClientId());
  }

  public UserCart getOrRegisterUserById(String id) {
    BackendUserCart user = new BackendUserCart(jedis, id, warehouse);
    registerUser(user);
    return user;
  }
}
