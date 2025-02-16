package ru.otus.springjms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.springjms.config.JmsConfig;
import ru.otus.springjms.service.MessageProducer;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final JmsConfig jmsConfig;

    private final MessageProducer messageProducer;

    @PostMapping
    public String sendMessage(@RequestBody String text) {
        messageProducer.sendMessage(jmsConfig.getQueue(), text);

        return "Message sent: " + text;
    }

}