package ru.bobko.shop.core.requestresponsecyclemanager;

import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.util.ThreadUtil;

import java.util.concurrent.TimeUnit;

public class RequestResponseCycleManagerWithTimeout implements RequestResponseCycleManager {
  private final RequestResponseCycleManager manager;
  private final long time;
  private final TimeUnit unit;

  private RequestResponseCycleManagerWithTimeout(RequestResponseCycleManager manager, long time, TimeUnit unit) {
    this.manager = manager;
    this.time = time;
    this.unit = unit;
  }

  @Override
  public void notifyResponseCome(Message message) {
    manager.notifyResponseCome(message);
  }

  @Override
  public Message requestResponseCycle(Message request) throws InterruptedException {
    return ThreadUtil.interruptSupplierIn(time, unit, () -> manager.requestResponseCycle(request));
  }

  public static RequestResponseCycleManager wrap(RequestResponseCycleManager manager, long time, TimeUnit unit) {
    return new RequestResponseCycleManagerWithTimeout(manager, time, unit);
  }
}
