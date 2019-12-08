package ru.bobko.shop.frontend.cli.base;

import java.util.Objects;

public class CliException extends RuntimeException {
  public CliException(String msg) {
    super(Objects.requireNonNull(msg));
  }

  public static class ServerDoesntRespondCliException extends CliException {

    public ServerDoesntRespondCliException() {
      super("Server doesn't respond");
    }
  }
}
