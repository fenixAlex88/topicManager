package ru.clevertec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TopicImplTest {
    private Topic topic;

    @BeforeEach
    void setUp() {
        topic = new TopicImpl("TestTopic");
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
    @DisplayName("Retrieve the condition for new messages")
    void testGetNewMessageCondition() {
        Condition condition = topic.getNewMessageCondition();
        assertNotNull(condition);
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
}