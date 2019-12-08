package ru.bobko.shop.core.model;

import ru.bobko.shop.core.model.good.Good;

import java.util.Map;

public interface UserCart {
  String getClientId();
  boolean add(Good good);
  boolean removeFromCart(Good good);
  Map<Good, Integer> buy();
  void discard();
  Map<Good, Integer> getCurrentGoodsInCart();
}
