package ru.bobko.shop.backend.di;

import com.rabbitmq.client.Channel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ru.bobko.shop.backend.model.Statistics;
import ru.bobko.shop.backend.model.BackendUsers;
import ru.bobko.shop.backend.model.BackendWarehouse;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RabbitMqRequestResponseCycleManager;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManagerWithTimeout;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BackendInjector implements Injector {

  private final Channel channel;
  private final Warehouse warehouse;
  private final RequestResponseCycleManager manager;
  private final BackendUsers users;
  private final Statistics statistics;

  public BackendInjector(Consumer<Message> rabbitMqMsgConsumer) {
    ChannelAndQueueNamePair pair = Injector.initChannel(Message.TO_BACKEND_ROUTING_KEY, rabbitMqMsgConsumer);
    channel = pair.channel;
    JedisPool pool = new JedisPool();
    try (Jedis jedis = pool.getResource()) {
      jedis.flushAll();
    }
    warehouse = new BackendWarehouse(pool);
    RabbitMqRequestResponseCycleManager rabbitMqManager = new RabbitMqRequestResponseCycleManager(channel);
    manager = RequestResponseCycleManagerWithTimeout.wrap(rabbitMqManager, 30, TimeUnit.SECONDS);
    statistics = new Statistics(pool, warehouse);
    users = new BackendUsers(pool, warehouse, statistics);
  }

  @Override
  public UserCart getCart() {
    throw new IllegalStateException("Doesn't make sense for backend");
  }

  @Override
  public Warehouse getWarehouse() {
    return warehouse;
  }

  @Override
  public Channel getChannel() {
    return channel;
  }

  /**
   * Backend uses it for heart beat
   */
  @Override
  public RequestResponseCycleManager getRequestResponseCycleManager() {
    return manager;
  }

  @Override
  public BackendUsers getUsers() {
    return users;
  }

  @Override
  public boolean isDebug() {
    return true;
  }

  @Override
  public Statistics getStatistics() {
    return statistics;
  }
}
