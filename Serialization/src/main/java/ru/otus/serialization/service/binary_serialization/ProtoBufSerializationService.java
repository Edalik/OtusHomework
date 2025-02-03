package ru.otus.serialization.service.binary_serialization;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Service;
import ru.otus.serialization.model.ProcessedSMS;
import ru.otus.serialization.model.ProcessedSMSProtos;
import ru.otus.serialization.service.interfaces.SerializationInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static ru.otus.serialization.config.SerializationConfig.OUTPUT_PATH;

@Service(ProtoBufSerializationService.SERVICE_NAME)
public class ProtoBufSerializationService<T, S extends ProcessedSMS> implements SerializationInterface<T, S> {

    public static final String SERVICE_NAME = "protobuf";

    @Override
    public void serialize(S object, Class<S> clazz) throws IOException {
        String path = String.format("%s/output.protobuf", OUTPUT_PATH);
        ProcessedSMSProtos.ProcessedSMS.Builder sms = ProcessedSMSProtos.ProcessedSMS.newBuilder();

        object.getMessages().forEach((key, entryValue) -> {
            ProcessedSMSProtos.Messages.Builder messages = ProcessedSMSProtos.Messages.newBuilder();

            entryValue.forEach(messageDetail -> {
                ProcessedSMSProtos.MessageDetail detail = ProcessedSMSProtos.MessageDetail.newBuilder()
                        .setChatIdentifier(messageDetail.getChatIdentifier())
                        .setLast(messageDetail.getLast())
                        .setText(messageDetail.getText())
                        .setSendDate(Timestamp.newBuilder()
                                .setNanos(messageDetail.getSendDate().getNanos())
                                .setSeconds(messageDetail.getSendDate().getTime())
                                .build())
                        .build();

                messages.addDetails(detail);
            });
            sms.putMessages(key, messages.build());
        });

        ProcessedSMSProtos.ProcessedSMS processedSMS = sms.build();
        processedSMS.writeTo(new BufferedOutputStream(new FileOutputStream(path)));

        ProcessedSMSProtos.ProcessedSMS deserialized = ProcessedSMSProtos.ProcessedSMS.newBuilder()
                .mergeFrom(new BufferedInputStream(new FileInputStream(path))).build();

        System.out.println(deserialized);
    }

    @Override
    public T deserialize(BufferedInputStream stream, Class<T> clazz) {
        return null;
    }

}