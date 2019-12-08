package ru.bobko.shop.frontend.cli.base;

/**
 * Represents ways cli could react on user input
 */
@FunctionalInterface
public interface CliCommandAction {
  String execAndGetOutput();
}
