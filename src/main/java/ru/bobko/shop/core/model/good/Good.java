package ru.bobko.shop.core.model.good;

import com.google.gson.Gson;

import java.util.Set;

public class Good {
  public final String vendorCode;
  public final String name;
  public final int price;
  public final Set<String> categories;

  public Good(String vendorCode, String name, int price, Set<String> categories) {
    this.vendorCode = vendorCode;
    this.name = name;
    this.price = price;
    this.categories = categories;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public static Good fromJson(String json) {
    return new Gson().fromJson(json, Good.class);
  }
}
