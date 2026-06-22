package com.unitel.fms.backend.dtos.response;

import com.unitel.fms.backend.constants.SystemMessage;
import com.unitel.fms.backend.contexts.SecurityContextHolder;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseData<T> implements Serializable {
    int status;
    String lang;
    String messageCode;
    T data;
    String error;
    String message;
    Date timestamp;
    String path;

    // Tự viết builder để kiểm soát message
    public static <T> ResponseDataBuilder<T> builder() {
        return new ResponseDataBuilder<>();
    }

    public static class ResponseDataBuilder<T> {
        private int status;
        private String lang = SecurityContextHolder.getLang();
        private String messageCode;
        private T data;
        private String error;
        private String message;
        private Date timestamp = new Date();
        private String path = SecurityContextHolder.getPath();

        public ResponseDataBuilder<T> status(int status) {
            this.status = status;
            return this;
        }

        public ResponseDataBuilder<T> lang(String lang) {
            this.lang = lang;
            return this;
        }

        public ResponseDataBuilder<T> messageCode(String messageCode) {
            this.messageCode = messageCode;
            return this;
        }

        public ResponseDataBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResponseDataBuilder<T> error(String error) {
            this.error = error;
            return this;
        }

        public ResponseDataBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResponseDataBuilder<T> timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ResponseDataBuilder<T> path(String path) {
            this.path = path;
            return this;
        }

        public ResponseData<T> build() {
            ResponseData<T> obj = new ResponseData<>();
            obj.status = this.status;
            obj.lang = this.lang;
            obj.messageCode = this.messageCode;
            obj.data = this.data;
            obj.error = this.error;
            obj.timestamp = this.timestamp;
            obj.path = this.path;
            obj.message = this.message != null ? this.message : obj.setMessageWithLangAndCode();
            return obj;
        }
    }

    public String setMessageWithLangAndCode() {
        if (this.messageCode == null) return null;
        String effectiveLang = this.lang != null ? this.lang : SecurityContextHolder.getLang();
        if (effectiveLang == null) effectiveLang = "en";
        return switch (effectiveLang.toLowerCase()) {
            case "vi" -> SystemMessage.VI.getOrDefault(messageCode, messageCode);
            case "lo" -> SystemMessage.LO.getOrDefault(messageCode, messageCode);
            default -> SystemMessage.EN.getOrDefault(messageCode, messageCode);
        };
    }
}
