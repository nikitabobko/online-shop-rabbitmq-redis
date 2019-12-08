package ru.bobko.shop.core.requestresponsecyclemanager;

import com.rabbitmq.client.Channel;
import ru.bobko.shop.core.model.message.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Thread safe
 */
public class RabbitMqRequestResponseCycleManager implements RequestResponseCycleManager {
  private final Channel rabbitMqChannel;
  private final Map<Integer, Message> responses = new HashMap<>();
  private final Object responsesHasNewKey = new Object();

  public RabbitMqRequestResponseCycleManager(Channel rabbitMqChannel) {
    this.rabbitMqChannel = rabbitMqChannel;
  }

  @Override
  public void notifyResponseCome(Message message) {
    synchronized (responsesHasNewKey) {
      responses.put(message.sessionId, message);
      responsesHasNewKey.notifyAll();
    }
  }

  @Override
  public Message requestResponseCycle(Message request) throws InterruptedException {
    try {
      request.sendToBackend(rabbitMqChannel);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    synchronized (responsesHasNewKey) {
      int sessionId = request.sessionId;
      while (!responses.containsKey(sessionId)) {
        responsesHasNewKey.wait();
      }
      return Objects.requireNonNull(responses.remove(sessionId));
    }
  }
}
