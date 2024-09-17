package ru.clevertec;

import java.util.concurrent.locks.Condition;

public interface Topic {
    void publish(Message message);
    Message consume(int index);
    int size();
    Condition getNewMessageCondition();
}
