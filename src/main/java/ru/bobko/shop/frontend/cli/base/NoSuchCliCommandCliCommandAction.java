package ru.bobko.shop.frontend.cli.base;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class NoSuchCliCommandCliCommandAction implements CliCommandAction {
  private final String actualUserCommand;

  public NoSuchCliCommandCliCommandAction(String actualUserCommand) {
    this.actualUserCommand = actualUserCommand;
  }

  @Override
  public String execAndGetOutput() {
    List<CliCommand> potentialCandidates = CliCommand.registered.stream()
      .filter(it -> it.getCommandName().contains(actualUserCommand))
      .collect(toList());
    if (potentialCandidates.size() == 1) {
      return "Do you mean?\n\n" + potentialCandidates.get(0).getHelp();
    } else {
      return HelpCliCommand.INSTANCE.execAndGetOutput();
    }
  }
}
