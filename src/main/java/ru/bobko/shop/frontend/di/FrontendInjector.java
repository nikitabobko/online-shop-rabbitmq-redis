package ru.bobko.shop.frontend.di;

import com.rabbitmq.client.Channel;
import ru.bobko.shop.backend.model.BackendUsers;
import ru.bobko.shop.backend.model.Statistics;
import ru.bobko.shop.core.requestresponsecyclemanager.RabbitMqRequestResponseCycleManager;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManagerWithTimeout;
import ru.bobko.shop.frontend.model.FrontendUserCart;
import ru.bobko.shop.frontend.model.FrontendWarehouse;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FrontendInjector implements Injector {

  private final Channel channel;
  private final UserCart cart;
  private final RequestResponseCycleManager manager;
  private final Warehouse warehouse;

  public FrontendInjector(Consumer<Message> incomingRabbitMqMsgConsumer) {
    ChannelAndQueueNamePair channelAndQueueNamePair = Injector.initChannel("", incomingRabbitMqMsgConsumer);
    channel = channelAndQueueNamePair.channel;
    manager = RequestResponseCycleManagerWithTimeout.wrap(new RabbitMqRequestResponseCycleManager(channel), 3, TimeUnit.SECONDS);
    String clientId = channelAndQueueNamePair.queueName;
    cart = new FrontendUserCart(manager, clientId);
    warehouse = new FrontendWarehouse(manager, clientId);
  }

  @Override
  public UserCart getCart() {
    return cart;
  }

  @Override
  public Warehouse getWarehouse() {
    return warehouse;
  }

  @Override
  public Channel getChannel() {
    return channel;
  }

  @Override
  public RequestResponseCycleManager getRequestResponseCycleManager() {
    return manager;
  }

  @Override
  public BackendUsers getUsers() {
    throw new IllegalStateException("Doesn't make sense for frontend");
  }

  @Override
  public boolean isDebug() {
    return false;
  }

  @Override
  public Statistics getStatistics() {
    throw new IllegalStateException("Doesn't make sense for frontend");
  }
}
