package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;

import java.util.Map;
import java.util.stream.Collectors;

public enum  ShowAllCliCommand implements CliCommand, CliCommandAction {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "show_all";
  }

  @Override
  public String getDescription() {
    return "Shows all available goods";
  }

  @Override
  public String getCommandUsage() {
    return getCommandName();
  }

  @Override
  public CliCommandAction commandToActionNullable(String command) {
    return CliCommand.commandToActionNoCliArgumentsNullable(this, command, this);
  }

  @Override
  public String execAndGetOutput() {
    Injector injector = InjectorHolder.getInjector();
    Warehouse warehouse = injector.getWarehouse();
    Map<Good, Integer> all = warehouse.getAll();
    if (all.isEmpty()) {
      return "No more goods left :(";
    }
    return all.entrySet().stream()
      .map(it -> it.getKey().toString() + "\nAmount: " + it.getValue())
      .collect(Collectors.joining("\n\n"));
  }
}
