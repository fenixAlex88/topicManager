package ru.clevertec.exception;

public class TopicInterruptedException extends RuntimeException {

    public TopicInterruptedException(String message) {
        super(message);
    }
}