package ru.bobko.shop.util;

import ru.bobko.shop.core.model.good.Good;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public enum CollectionUtil {
  ;

  public static String toString(Map<Good, Integer> goods) {
    return goods.entrySet().stream()
      .sorted(Comparator.comparing(it -> it.getKey().name))
      .map(it -> it.getKey().toString() + "\nAmount: " + it.getValue())
      .collect(Collectors.joining("\n\n"));
  }
}
