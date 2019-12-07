package ru.bobko.shop.frontend.cli.command;

import ru.bobko.shop.frontend.cli.command.base.CliCommand;
import ru.bobko.shop.frontend.cli.command.base.CliCommandAction;

import static ru.bobko.shop.frontend.cli.command.base.CliCommand.oneCliArgumentCommandToActionOrNull;

public enum AddToCardCliCommand implements CliCommand {
  INSTANCE;
  @Override
  public String getCommandName() {
    return "add_to_cart";
  }

  @Override
  public String getCommandUsage() {
    return String.format("%s good_vendor_code", getCommandName());
  }

  @Override
  public String getDescription() {
    return "Adds good to current cart";
  }

  @Override
  public CliCommandAction commandToActionOrNull(String command) {
    return oneCliArgumentCommandToActionOrNull(this, command, AddToCardCliCommandAction::new);
  }

  public static class AddToCardCliCommandAction implements CliCommandAction {
    private final String vendorCode;

    public AddToCardCliCommandAction(String vendorCode) {
      this.vendorCode = vendorCode;
    }

    @Override
    public void exec() {

    }
  }
}
