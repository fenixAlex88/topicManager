package ru.clevertec.service.topicServiceImpl;

import ru.clevertec.entity.Message;
import ru.clevertec.service.TopicService;
import ru.clevertec.exception.TopicInterruptedException;
import ru.clevertec.exception.TopicTimeoutException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TopicServiceImpl implements TopicService {
    private final List<Message> messages = new ArrayList<>();
    private final Lock lock;
    private final Condition newMessageCondition;

    public TopicServiceImpl(String name) {
        lock = new ReentrantLock();
        newMessageCondition = lock.newCondition();
    }

    @Override
    public void publish(Message message) {
        if (message == null) {
            throw new NullPointerException("Message cannot be null");
        }
        lock.lock();
        try {
            messages.add(message);
            newMessageCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message consume(int index) {
        lock.lock();
        try {
            while (index >= size()) {
                try {
                    if (!newMessageCondition.await(5, TimeUnit.SECONDS)) {
                        throw new TopicTimeoutException("Timeout while waiting for message");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new TopicInterruptedException("Thread was interrupted");
                }
            }
            if (index < 0) {
                throw new IndexOutOfBoundsException("Invalid index: " + index);
            }
            return messages.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return messages.size();
        } finally {
            lock.unlock();
        }
    }
}