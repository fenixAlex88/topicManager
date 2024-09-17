package ru.clevertec;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface Consumer {
    void consumeMessage();
    void setMessageList(List<Message> messageList);
    void setCountDownLatch(CountDownLatch countDownLatch);
}
