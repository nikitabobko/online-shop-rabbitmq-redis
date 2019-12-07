package ru.bobko.shop.core.model;

import ru.bobko.shop.core.model.good.Good;

import java.util.Map;

public interface UserCart {
  void add(Good good);
  void removeFromCart(Good good);
  void buy();
  void discard();
  Map<Good, Integer> getCurrentGoodsInCart();
}
