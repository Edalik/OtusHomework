package ru.otus.springjms.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.otus.springjms.model.dto.Message;

@Service
public class MessageConsumer {

    @JmsListener(destination = "${spring.activemq.queue:message-queue}")
    public void receiveMessage(Message message) {
        System.out.println("Received message: " + message.getUuid() + " - " + message.getText());
    }

}