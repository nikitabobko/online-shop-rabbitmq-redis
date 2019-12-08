package ru.bobko.shop.backend;

import com.rabbitmq.client.Channel;
import ru.bobko.shop.core.di.Injector;

import java.io.IOException;

// todo remove
public class Send {
  public static void main(String[] args) throws IOException {
    Injector.ChannelAndQueueNamePair foo = Injector.initChannel("foo", (msg) -> {

    });
    Channel channel = foo.channel;
    channel.basicPublish("", "backend", null, "suka".getBytes());
  }
}
