package ru.otus.serialization.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.otus.serialization.util.NanosecondsTimestampDeserializer;
import ru.otus.serialization.util.NanosecondsTimestampSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
public class SMS implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("chat_sessions")
    private List<ChatSession> chatSessions;

    @Data
    public static class ChatSession implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("chat_id")
        private Integer chatId;

        @JsonProperty("chat_identifier")
        private String chatIdentifier;

        @JsonProperty("display_name")
        private String displayName;

        @JsonProperty("is_deleted")
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Boolean isDeleted;

        @JsonProperty("members")
        private List<Member> members;

        @JsonProperty("messages")
        private List<Message> messages;

    }


    @Data
    public static class Member implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("first")
        private String first;

        @JsonProperty("handle_id")
        private Integer handleId;

        @JsonProperty("image_path")
        private String imagePath;

        @JsonProperty("last")
        private String last;

        @JsonProperty("middle")
        private String middle;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("service")
        private String service;

        @JsonProperty("thumb_path")
        private String thumbPath;

    }


    @Data
    public static class Message implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("ROWID")
        private Integer rowId;

        @JsonProperty("attributedBody")
        private String attributedBody;

        @JsonProperty("belong_number")
        private String belongNumber;

        @JsonProperty("date")
        @JsonSerialize(using = NanosecondsTimestampSerializer.class)
        @JsonDeserialize(using = NanosecondsTimestampDeserializer.class)
        private Timestamp date;

        @JsonProperty("date_read")
        @JsonSerialize(using = NanosecondsTimestampSerializer.class)
        @JsonDeserialize(using = NanosecondsTimestampDeserializer.class)
        private Timestamp dateRead;

        @JsonProperty("guid")
        private String guid;

        @JsonProperty("handle_id")
        private Integer handleId;

        @JsonProperty("has_dd_results")
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Boolean hasDdResults;

        @JsonProperty("is_deleted")
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Boolean isDeleted;

        @JsonProperty("is_from_me")
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Boolean isFromMe;

        @JsonProperty("send_date")
        @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
        private Timestamp sendDate;

        @JsonProperty("send_status")
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Boolean sendStatus;

        @JsonProperty("service")
        private String service;

        @JsonProperty("text")
        private String text;

    }

}