package ru.clevertec.consumer;

import ru.clevertec.entity.Message;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface Consumer {
    void consumeMessage();
    void setMessageList(List<Message> messageList);
    void setCountDownLatch(CountDownLatch countDownLatch);
}
