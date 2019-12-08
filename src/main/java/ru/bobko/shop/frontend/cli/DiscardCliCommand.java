package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;

public enum DiscardCliCommand implements CliCommand, CliCommandAction {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "discard";
  }

  @Override
  public String getDescription() {
    return "Discards current cart";
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
    UserCart cart = injector.getCart();
    cart.discard();
    return "Current cart is discarded";
  }
}
