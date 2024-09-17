package ru.clevertec.ru.clevertec.consumer.consumerImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.clevertec.consumer.consumerImpl.ConsumerImpl;
import ru.clevertec.entity.Message;
import ru.clevertec.service.TopicService;
import ru.clevertec.service.topicServiceImpl.TopicServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ConsumerImplTest {
    private TopicService topic;
    private ConsumerImpl consumer;
    private List<Message> messageList;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        topic = new TopicServiceImpl("TestTopic");
        consumer = new ConsumerImpl(topic);
        messageList = new ArrayList<>();
        countDownLatch = new CountDownLatch(2);
    }

    @Test
    @DisplayName("Consuming messages")
    void testConsumeMessages() throws InterruptedException {
        consumer.setMessageList(messageList);
        consumer.setCountDownLatch(countDownLatch);

        Message message1 = new Message("TestMessage1");
        Message message2 = new Message("TestMessage2");
        topic.publish(message1);
        topic.publish(message2);

        consumer.consumeMessage();
        countDownLatch.await();

        assertEquals(2, messageList.size());
        assertEquals("TestMessage1", messageList.get(0).content());
        assertEquals("TestMessage2", messageList.get(1).content());
    }

    @Test
    @DisplayName("CountDownLatch decrement")
    void testCountDownLatchDecrement() throws InterruptedException {
        consumer.setMessageList(messageList);
        consumer.setCountDownLatch(countDownLatch);

        Message message1 = new Message("TestMessage1");
        Message message2 = new Message("TestMessage2");
        topic.publish(message1);
        topic.publish(message2);

        consumer.consumeMessage();
        countDownLatch.await();

        assertEquals(0, countDownLatch.getCount());
    }

    @Test
    @DisplayName("Consume messages and verify their presence in the message list")
    void testConsumeMessage() throws InterruptedException {
        consumer.setMessageList(messageList);
        consumer.consumeMessage();

        topic.publish(new Message("TestMessage1"));
        topic.publish(new Message("TestMessage2"));
        topic.publish(new Message("TestMessage3"));

        TimeUnit.MILLISECONDS.sleep(100);

        assertEquals(3, messageList.size());
        assertEquals("TestMessage1", messageList.get(0).content());
        assertEquals("TestMessage2", messageList.get(1).content());
        assertEquals("TestMessage3", messageList.get(2).content());
    }

    @Test
    @DisplayName("Consume messages without setting message list")
    void testConsumeMessagesWithoutMessageList() throws InterruptedException {
        TopicService mockTopic = Mockito.mock(TopicService.class);
        consumer = new ConsumerImpl(mockTopic);
        consumer.setCountDownLatch(countDownLatch);

        Message message1 = new Message("TestMessage1");
        Message message2 = new Message("TestMessage2");
        when(mockTopic.size()).thenReturn(2);
        when(mockTopic.consume(0)).thenReturn(message1);
        when(mockTopic.consume(1)).thenReturn(message2);

        consumer.consumeMessage();
        countDownLatch.await();

        verify(mockTopic, times(2)).consume(anyInt());
    }

    @Test
    @DisplayName("Consume null messages")
    void testConsumeNullMessages() throws InterruptedException {
        consumer.setMessageList(messageList);
        consumer.consumeMessage();

        topic.publish(new Message(null));

        TimeUnit.MILLISECONDS.sleep(300);

        assertEquals(0, messageList.size());
    }

    @Test
    @DisplayName("Multiple topics and 100 consumers")
    void testMultipleTopicsAndConsumers() throws InterruptedException {
        final int NUM_TOPICS = 10;
        final int NUM_CONSUMERS = 100;
        final int MESSAGES_PER_TOPIC = 10;


        List<TopicService> topics = new ArrayList<>();
        List<ConsumerImpl> consumers = new ArrayList<>();
        List<List<Message>> messageLists = new ArrayList<>();

        for (int i = 0; i < NUM_TOPICS; i++) {
            TopicService topic = new TopicServiceImpl("Topic" + i);
            topics.add(topic);
        }

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            TopicService topic = topics.get(i % NUM_TOPICS);
            ConsumerImpl consumer = new ConsumerImpl(topic);
            consumers.add(consumer);

            List<Message> messageList = new ArrayList<>();
            messageLists.add(messageList);

            consumer.setMessageList(messageList);
        }

        for (int i = 0; i < NUM_TOPICS; i++) {
            TopicService topic = topics.get(i);
            for (int j = 0; j < MESSAGES_PER_TOPIC; j++) {
                topic.publish(new Message("Message" + j + " for Topic" + i));
            }
        }

        for (ConsumerImpl consumer : consumers) {
            consumer.consumeMessage();
        }

        TimeUnit.MILLISECONDS.sleep(500);

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            List<Message> expectedMessages = new ArrayList<>();
            for (int j = 0; j < MESSAGES_PER_TOPIC; j++) {
                expectedMessages.add(new Message("Message" + j + " for Topic" + (i % NUM_TOPICS)));
            }
            assertEquals(expectedMessages, messageLists.get(i));
        }
    }
}
