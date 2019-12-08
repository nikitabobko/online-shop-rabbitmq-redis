package ru.bobko.shop.core.model;

import ru.bobko.shop.core.model.good.Good;

import java.util.Map;

public interface Warehouse {
  Good getGoodByVendorCodeNullable(String vendorCode);
  Map<Good, Integer> getAll();
  int amountOf(Good good);
}
