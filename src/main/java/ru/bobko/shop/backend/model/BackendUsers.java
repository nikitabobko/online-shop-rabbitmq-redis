package ru.bobko.shop.backend.model;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BackendUsers {
  private final JedisPool pool;
  private final static String CLIENT_IDS_REDIS_KEY = "client_ids";
  private final Warehouse warehouse;
  private final Statistics statistics;

  public BackendUsers(JedisPool pool, Warehouse warehouse, Statistics statistics) {
    this.pool = pool;
    this.warehouse = warehouse;
    this.statistics = statistics;
  }

  public Set<UserCart> getAllUsers() {
    try (Jedis jedis = pool.getResource()) {
      return jedis.smembers(CLIENT_IDS_REDIS_KEY).stream().map(it -> new BackendUserCart(jedis, it, warehouse)).collect(toSet());
    }
  }

  public void registerUser(UserCart cart) {
    try (Jedis jedis = pool.getResource()) {
      jedis.sadd(CLIENT_IDS_REDIS_KEY, cart.getClientId());
    }
  }

  public void removeUser(UserCart user) {
    user.discard();
    try (Jedis jedis = pool.getResource()) {
      jedis.srem(CLIENT_IDS_REDIS_KEY, user.getClientId());
    }
    statistics.reportUserRemoved(user);
  }

  public UserCart getOrRegisterUserById(String id) {
    try (Jedis jedis = pool.getResource()) {
      BackendUserCart user = new BackendUserCart(jedis, id, warehouse);
      registerUser(user);
      return user;
    }
  }
}
