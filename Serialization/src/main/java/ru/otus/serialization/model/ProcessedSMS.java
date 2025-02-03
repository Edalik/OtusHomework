package ru.otus.serialization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

@Data
public class ProcessedSMS implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<String, SortedSet<MessageDetail>> messages = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDetail implements Comparable<MessageDetail>, Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("chat_identifier")
        String chatIdentifier;

        @JsonProperty("last")
        String last;

        @JsonProperty("send_date")
        Timestamp sendDate;

        @JsonProperty("text")
        String text;

        @Override
        public int compareTo(MessageDetail o) {
            int diff = sendDate.compareTo(o.sendDate);
            if (diff == 0) {
                return hashCode() - o.hashCode();
            }

            return diff;
        }

    }

}