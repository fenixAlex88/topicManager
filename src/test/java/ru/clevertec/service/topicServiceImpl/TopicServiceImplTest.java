package ru.clevertec.service.topicServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.clevertec.entity.Message;
import ru.clevertec.exception.TopicTimeoutException;
import ru.clevertec.service.TopicService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

public class TopicServiceImplTest {
    private TopicService topic;

    @BeforeEach
    void setUp() {
        topic = new TopicServiceImpl("TestTopic");
    }

    @Test
    @DisplayName("Publish a message and verify its presence in the topic")
    void testPublishMessage() {
        Message message = new Message("TestMessage");
        topic.publish(message);
        assertEquals(1, topic.size());
        assertEquals("TestMessage", topic.consume(0).content());
    }

    @Test
    @DisplayName("Publish and retrieve multiple messages")
    void testGetMessage() {
        Message message1 = new Message("TestMessage1");
        Message message2 = new Message("TestMessage2");
        topic.publish(message1);
        topic.publish(message2);
        assertEquals("TestMessage1", topic.consume(0).content());
        assertEquals("TestMessage2", topic.consume(1).content());
    }

    @Test
    @DisplayName("Check the topic size after publishing messages")
    void testSize() {
        assertEquals(0, topic.size());
        topic.publish(new Message("TestMessage1"));
        assertEquals(1, topic.size());
        topic.publish(new Message("TestMessage2"));
        assertEquals(2, topic.size());
    }

    @Test
    @DisplayName("Consume message with invalid index")
    void testConsumeInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> topic.consume(-1));
    }

    @Test
    @DisplayName("Publish null message")
    void testPublishNullMessage() {
        assertThrows(NullPointerException.class, () -> topic.publish(null));
    }

    @Test
    @DisplayName("Timeout while waiting for message")
    void testTimeoutWhileWaitingForMessage() {
        assertThrows(TopicTimeoutException.class, () -> topic.consume(0));
    }

    @Test
    @DisplayName("Concurrent message publishing")
    void testConcurrentPublish() throws InterruptedException {
        Runnable publishTask = () -> {
            for (int i = 0; i < 100; i++) {
                topic.publish(new Message("Message " + i));
            }
        };

        Thread thread1 = new Thread(publishTask);
        Thread thread2 = new Thread(publishTask);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertEquals(200, topic.size());
    }

    @Test
    @DisplayName("Consume message with interrupted exception")
    void testConsumeInterruptedException() {
        TopicServiceImpl topicSpy = Mockito.spy(new TopicServiceImpl("TestTopic"));
        doReturn(0).when(topicSpy).size();

        Thread.currentThread().interrupt();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> topicSpy.consume(0));

        assertEquals("Thread was interrupted", exception.getMessage());

        Thread.interrupted();
    }
}
