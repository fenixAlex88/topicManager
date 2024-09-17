package ru.clevertec.service;

import ru.clevertec.entity.Message;

public interface TopicService {
    void publish(Message message);
    Message consume(int index);
    int size();
}
