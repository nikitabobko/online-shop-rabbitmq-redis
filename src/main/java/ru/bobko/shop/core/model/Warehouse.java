package ru.bobko.shop.core.model;

import ru.bobko.shop.core.model.good.Good;

import java.util.Map;

public interface Warehouse {
  Good getGoodByVendorCodeNullable(String vendorCode);
  Map<Good, Integer> getAll();
  int amountOf(Good good);
  void setAmountOf(Good good, int amount);
  void addGood(Good good, int count);
  void clearGood(Good good);
}
