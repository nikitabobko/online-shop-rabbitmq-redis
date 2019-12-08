package ru.bobko.shop.core.requestresponsecyclemanager;

import ru.bobko.shop.core.model.message.Message;

public interface RequestResponseCycleManager {
    void notifyResponseCome(Message message);

    default Message requestResponseCycleToBackend(Message request) throws InterruptedException {
      return requestResponseCycle(Message.TO_BACKEND_ROUTING_KEY, request);
    }

    Message requestResponseCycle(String clientId, Message msg) throws InterruptedException;
}
