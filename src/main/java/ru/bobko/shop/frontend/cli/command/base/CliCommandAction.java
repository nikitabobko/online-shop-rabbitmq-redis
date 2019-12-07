package ru.bobko.shop.frontend.cli.command.base;

import java.util.Arrays;
import java.util.stream.Collectors;

@FunctionalInterface
public interface CliCommandAction {
  void exec();

  static void printResponse(String response) {
    System.out.println(Arrays.stream(response.split("\n")).map(it -> "  " + it).collect(Collectors.joining("\n")));
  }
}
