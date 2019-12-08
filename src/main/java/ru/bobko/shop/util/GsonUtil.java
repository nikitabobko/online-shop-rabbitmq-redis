package ru.bobko.shop.util;

import com.google.gson.Gson;
import ru.bobko.shop.core.model.good.Good;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public enum GsonUtil {
  ;

  public static Map<Good, Integer> decodeGoodsMap(String str) {
    Gson gson = new Gson();
    @SuppressWarnings("unchecked")
    List<String> list = gson.fromJson(str, List.class);
    return list.stream()
      .map(it -> gson.fromJson(it, Pair.class))
      .collect(toMap(
        it -> gson.fromJson(it.first.toString(), Good.class),
        it -> (int) Double.parseDouble(it.second.toString())));
  }

  public static String encodeGoodsMap(Map<Good, Integer> map) {
    Gson gson = new Gson();
    List<String> list = map.entrySet().stream()
      .map(it -> new Pair<>(gson.toJson(it.getKey()), gson.toJson(it.getValue())))
      .map(gson::toJson).collect(toList());
    return gson.toJson(list);
  }
}
