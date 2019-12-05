package ru.bobko.shop;

import redis.clients.jedis.Jedis;

public class Main {
  public static void main(String[] args) {
    Jedis jedis = new Jedis();
    jedis.set("foo", "bar");
    System.out.println(jedis.get("foo"));
  }
}
