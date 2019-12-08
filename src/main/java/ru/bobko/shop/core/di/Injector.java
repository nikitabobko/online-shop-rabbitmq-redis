package ru.bobko.shop.core.di;

import com.rabbitmq.client.*;
import ru.bobko.shop.backend.model.BackendUsers;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public interface Injector {
  UserCart getCart();
  Warehouse getWarehouse();
  Channel getChannel();
  RequestResponseCycleManager getRequestResponseCycleManager();
  BackendUsers getUsers();
  boolean isDebug();

  // protected
  static ChannelAndQueueNamePair initChannel(String queueName, Consumer<Message> incomingRabbitMqMsgConsumer) {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();
      AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, false, true, true, null);
      queueName = declareOk.getQueue();
      DeliverCallback callback = (consumerTag, delivery) -> {
        if (!InjectorHolder.isInitialized()) {
          // Don't allow to handle msgs until injector isn't initialized
          System.out.println("Skipping json");
          return;
        }
        String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
        if (InjectorHolder.getInjector().isDebug()) {
          System.out.println("Got json: " + json);
        }
        incomingRabbitMqMsgConsumer.accept(Message.fromJson(json));
      };
      CancelCallback cancelCallback = consumerTag -> System.exit(1);
      channel.basicConsume(queueName, callback, cancelCallback);
      return new ChannelAndQueueNamePair(channel, queueName);
    } catch (IOException | TimeoutException ex) {
      System.exit(1);
      throw new IllegalStateException("Expected to exit");
    }
  }

  class ChannelAndQueueNamePair {
    public final Channel channel;
    public final String queueName;

    public ChannelAndQueueNamePair(Channel channel, String queueName) {
      this.channel = channel;
      this.queueName = queueName;
    }
  }
}
