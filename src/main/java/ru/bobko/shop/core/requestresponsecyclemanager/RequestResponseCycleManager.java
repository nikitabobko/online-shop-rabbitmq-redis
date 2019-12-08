package ru.bobko.shop.core.requestresponsecyclemanager;

import ru.bobko.shop.core.model.message.Message;

public interface RequestResponseCycleManager {
    void notifyResponseCome(Message message);

    Message requestResponseCycle(Message request) throws InterruptedException;
}
