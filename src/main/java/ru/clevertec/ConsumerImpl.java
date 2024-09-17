package ru.clevertec;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ConsumerImpl implements Consumer, Runnable {
    private final Topic topic;
    private int lastReadIndex;
    private List<Message> messageList;
    private CountDownLatch countDownLatch;

    public ConsumerImpl(Topic topic) {
        this.topic = topic;
        this.lastReadIndex = 0;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        while (countDownLatch == null || countDownLatch.getCount() > 0) {
            while (lastReadIndex >= topic.size()) {
                try {
                    topic.getNewMessageCondition().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Message message = topic.consume(lastReadIndex);
            if (message != null) {
                if (messageList !=null)
                    messageList.add(message);
                lastReadIndex++;
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }

        }
    }

    public void consumeMessage() {
        new Thread(this).start();
    }
}

