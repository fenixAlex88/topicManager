package ru.clevertec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TopicImpl implements Topic {
    private final List<Message> messages = new ArrayList<>();
    private final Lock lock;
    private final Condition newMessageCondition;

    public TopicImpl(String name) {
        lock = new ReentrantLock();
        newMessageCondition = lock.newCondition();
    }

    @Override
    public void publish(Message message) {
        lock.lock();
        try {
            this.messages.add(message);
            newMessageCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Condition getNewMessageCondition() {
        return newMessageCondition;
    }

    @Override
    public Message consume(int index) {
        lock.lock();
        try {
            return this.messages.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return this.messages.size();
        } finally {
            lock.unlock();
        }
    }
}
