package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;

import java.util.Set;
import java.util.stream.Collectors;

public enum ShowCategoryCliCommand implements CliCommand {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "show_category";
  }

  @Override
  public String getDescription() {
    return "Shows goods from selected category";
  }

  @Override
  public String getCommandUsage() {
    return String.format("%s category_name", getCommandName());
  }

  @Override
  public CliCommandAction commandToActionNullable(String command) {
    return CliCommand.commandToActionOneCliArgumentNullable(this, command, ShowCategoryCliCommandAction::new);
  }

  private static class ShowCategoryCliCommandAction implements CliCommandAction {
    private final String selectedCategory;

    private ShowCategoryCliCommandAction(String selectedCategory) {
      this.selectedCategory = selectedCategory;
    }

    @Override
    public String execAndGetOutput() {
      Injector injector = InjectorHolder.getInjector();
      Warehouse warehouse = injector.getWarehouse();
      Set<Good> goods = warehouse.showCategory(selectedCategory);
      String goodsJoined = goods.stream().map(Good::toString).collect(Collectors.joining("\n"));
      return "Goods matching " + selectedCategory + " category:\n" + goodsJoined;
    }
  }
}
