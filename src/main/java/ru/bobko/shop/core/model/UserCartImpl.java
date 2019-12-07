package ru.bobko.shop.core.model;

import redis.clients.jedis.commands.JedisCommands;
import ru.bobko.shop.core.model.good.Good;

import java.util.Map;

public class UserCartImpl implements UserCart {
  private final JedisCommands jedis;

  public UserCartImpl(JedisCommands jedis) {
    this.jedis = jedis;
  }

  @Override
  public void add(Good good) {

  }

  @Override
  public void removeFromCart(Good good) {

  }

  @Override
  public void buy() {

  }

  @Override
  public void discard() {

  }

  @Override
  public Map<Good, Integer> getCurrentGoodsInCart() {
    return null;
  }
}
