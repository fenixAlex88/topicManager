package ru.clevertec.exception;

public class TopicTimeoutException extends RuntimeException {

    public TopicTimeoutException(String message) {
        super(message);
    }
}