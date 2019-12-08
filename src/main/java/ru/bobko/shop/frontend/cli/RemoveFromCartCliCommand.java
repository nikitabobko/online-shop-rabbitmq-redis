package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;

public enum RemoveFromCartCliCommand implements CliCommand {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "rem_from_cart";
  }

  @Override
  public String getDescription() {
    return "Removes good from cart";
  }

  @Override
  public String getCommandUsage() {
    return String.format("%s vendor_code", getCommandName());
  }

  @Override
  public CliCommandAction commandToActionNullable(String command) {
    return CliCommand.commandToActionOneCliArgumentNullable(this, command, RemoveFromCartCliCommandAction::new);
  }

  private static class RemoveFromCartCliCommandAction implements CliCommandAction {
    private final String vendorCode;

    private RemoveFromCartCliCommandAction(String vendorCode) {
      this.vendorCode = vendorCode;
    }

    @Override
    public String execAndGetOutput() {
      Injector injector = InjectorHolder.getInjector();
      Warehouse warehouse = injector.getWarehouse();
      UserCart cart = injector.getCart();
      Good good = warehouse.getGoodByVendorCodeNullable(vendorCode);
      if (good == null) {
        return "No such vendor_code";
      }
      return cart.removeFromCart(good) ? "Removed" : "Failed";
    }
  }
}
