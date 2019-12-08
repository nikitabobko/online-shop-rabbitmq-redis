package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;
import ru.bobko.shop.frontend.cli.base.CliCommand;

import static ru.bobko.shop.frontend.cli.base.CliCommand.commandToActionOneCliArgumentNullable;

public enum AddToCardCliCommand implements CliCommand {
  INSTANCE;
  @Override
  public String getCommandName() {
    return "add_to_cart";
  }

  @Override
  public String getCommandUsage() {
    return String.format("%s vendor_code", getCommandName());
  }

  @Override
  public String getDescription() {
    return "Adds good to current cart";
  }

  @Override
  public CliCommandAction commandToActionNullable(String command) {
    return commandToActionOneCliArgumentNullable(this, command, AddToCardCliCommandAction::new);
  }

  private static class AddToCardCliCommandAction implements CliCommandAction {
    private final String vendorCode;

    public AddToCardCliCommandAction(String vendorCode) {
      this.vendorCode = vendorCode;
    }

    @Override
    public String execAndGetOutput() {
      Injector injector = InjectorHolder.getInjector();
      Warehouse warehouse = injector.getWarehouse();
      Good good = warehouse.getGoodByVendorCodeNullable(vendorCode);
      if (good == null) {
        return "No such vendor_code";
      }
      UserCart cart = injector.getCart();
      return cart.add(good) ? "Added" : "Failed";
    }
  }
}
