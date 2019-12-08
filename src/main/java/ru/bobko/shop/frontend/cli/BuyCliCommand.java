package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;
import ru.bobko.shop.util.GoodUtil;

import java.util.Map;

public enum BuyCliCommand implements CliCommand, CliCommandAction {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "buy";
  }

  @Override
  public String getDescription() {
    return "Buys current cart";
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
    Map<Good, Integer> bought = cart.buy();
    if (bought.isEmpty()) {
      return "Cart is empty";
    }
    return "Congrats! You bought:\n\n" + GoodUtil.toString(bought);
  }
}
