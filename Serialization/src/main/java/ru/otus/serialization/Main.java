package ru.otus.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import ru.otus.serialization.exception.BusinessException;
import ru.otus.serialization.model.ProcessedSMS;
import ru.otus.serialization.model.SMS;
import ru.otus.serialization.service.interfaces.SerializationInterface;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Main implements CommandLineRunner {

    private final ObjectMapper objectMapper;

    private final List<SerializationInterface<SMS, ProcessedSMS>> serializationServices;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        SMS sms = objectMapper.readValue(ResourceUtils.getFile("classpath:input/sms.json"), SMS.class);
        ProcessedSMS processedSMS = new ProcessedSMS();

        sms.getChatSessions().forEach(session ->
                session.getMessages().forEach(message -> {
                    ProcessedSMS.MessageDetail messageDetail = new ProcessedSMS.MessageDetail(
                            session.getChatIdentifier(),
                            session.getMembers().stream().findFirst().orElseThrow(NoSuchElementException::new).getLast(),
                            message.getSendDate(),
                            message.getText()
                    );

                    processedSMS.getMessages()
                            .computeIfAbsent(message.getBelongNumber(), k -> new TreeSet<>())
                            .add(messageDetail);
                })
        );

        serializationServices.forEach(service -> {
            try {
                service.serialize(processedSMS, ProcessedSMS.class);
            } catch (IOException e) {
                log.error("Error serializing", e);
                throw new BusinessException("Error serializing");
            }
        });
    }

}