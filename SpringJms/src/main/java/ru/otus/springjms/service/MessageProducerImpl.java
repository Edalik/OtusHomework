package ru.otus.springjms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.otus.springjms.model.dto.Message;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageProducerImpl implements MessageProducer {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String destination, String text) {
        Message message = new Message(UUID.randomUUID(), text);
        jmsTemplate.convertAndSend(destination, message);
    }

}