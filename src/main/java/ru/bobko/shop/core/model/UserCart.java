package ru.bobko.shop.core.model;

import ru.bobko.shop.core.model.good.Good;

import java.util.List;

public interface UserCart {
  void add(Good good);
  void removeFromCart(Good good);
  void buy();
  void discard();
  List<Good> getCurrentGoodsInCart();
}
