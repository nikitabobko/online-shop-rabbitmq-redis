package ru.bobko.shop.core.model.good;

import ru.bobko.shop.core.model.good.category.GoodCategory;

import java.util.List;

public interface Good {
  int getVendorCode();
  String getName();
  int getPrice();
  List<GoodCategory> getCategories();
}
