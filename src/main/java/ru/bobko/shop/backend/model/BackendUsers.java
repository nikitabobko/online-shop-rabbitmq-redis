package ru.bobko.shop.backend.model;

import redis.clients.jedis.Jedis;
import ru.bobko.shop.core.model.BackendUserCart;
import ru.bobko.shop.core.model.UserCart;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BackendUsers {
  private final Jedis jedis;
  private final static String CLIENT_IDS_REDIS_KEY = "client_ids";

  public BackendUsers(Jedis jedis) {
    this.jedis = jedis;
  }

  public Set<UserCart> getAllUsers() {
    return jedis.smembers(CLIENT_IDS_REDIS_KEY).stream().map(it -> new BackendUserCart(jedis, it)).collect(toSet());
  }

  public void registerUser(UserCart cart) {
    jedis.sadd(CLIENT_IDS_REDIS_KEY, cart.getClientId());
  }

  public UserCart getOrRegisterUserById(String id) {
    BackendUserCart user = new BackendUserCart(jedis, id);
    registerUser(user);
    return user;
  }
}
