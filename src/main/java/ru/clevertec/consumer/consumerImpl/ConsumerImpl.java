package ru.clevertec.consumer.consumerImpl;

import ru.clevertec.consumer.Consumer;
import ru.clevertec.entity.Message;
import ru.clevertec.service.TopicService;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ConsumerImpl implements Consumer, Runnable {
    private final TopicService topic;
    private int lastReadIndex;
    private List<Message> messageList;
    private CountDownLatch countDownLatch;

    public ConsumerImpl(TopicService topic) {
        this.topic = topic;
        this.lastReadIndex = 0;
    }

    @Override
    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        while (countDownLatch == null || countDownLatch.getCount() > 0) {
            Message message = topic.consume(lastReadIndex);
            if (message.content() != null) {
                if (messageList !=null)
                    messageList.add(message);
                lastReadIndex++;
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }

        }
    }
    @Override
    public void consumeMessage() {
        new Thread(this).start();
    }
}

