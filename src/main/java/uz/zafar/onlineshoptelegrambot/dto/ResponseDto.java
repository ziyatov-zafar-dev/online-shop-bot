package uz.zafar.onlineshoptelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;

public class ResponseDto<T> {

    private boolean success;
    private ErrorCode errorCode;
    private T data;
    private Message message;

    public ResponseDto() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseDto(boolean success, Message message, ErrorCode errorCode, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.data = data;
        this.message = message;
    }


    public static <T> ResponseDto<T> error(ErrorCode errorCode) {
        return new ResponseDto<>(false, message(errorCode), errorCode, null);
    }

    public static <T> ResponseDto<T> error(String error) {
        return new ResponseDto<>(false, message(error), null, null);
    }

    public static <T> ResponseDto<T> error(ErrorCode errorCode, T data) {
        return new ResponseDto<>(false, message(errorCode), errorCode, data);
    }

    private static Message message(ErrorCode errorCode) {
        return new Message(
                errorCode.getDescriptionUz(),
                errorCode.getDescriptionCyrillic(),
                errorCode.getDescriptionRu(),
                errorCode.getDescriptionEn()
        );
    }

    private static Message message(String errorCode) {
        return new Message(
                errorCode, errorCode, errorCode, errorCode
        );
    }

    public static <T> ResponseDto<T> success() {
        return new ResponseDto<>(true, message(ErrorCode.NONE), ErrorCode.NONE, null);
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, message(ErrorCode.NONE), ErrorCode.NONE, data);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Message {
        @JsonProperty("uz")
        private String messageUz;
        @JsonProperty("cyrillic")
        private String messageCyrillic;
        @JsonProperty("ru")
        private String messageRu;
        @JsonProperty("en")
        private String messageEn;

        public String getMessageUz() {
            return messageUz;
        }

        public void setMessageUz(String messageUz) {
            this.messageUz = messageUz;
        }

        public String getMessageCyrillic() {
            return messageCyrillic;
        }

        public void setMessageCyrillic(String messageCyrillic) {
            this.messageCyrillic = messageCyrillic;
        }

        public String getMessageRu() {
            return messageRu;
        }

        public void setMessageRu(String messageRu) {
            this.messageRu = messageRu;
        }

        public String getMessageEn() {
            return messageEn;
        }

        public void setMessageEn(String messageEn) {
            this.messageEn = messageEn;
        }

        public Message(String messageUz, String messageCyrillic, String messageRu, String messageEn) {
            this.messageUz = messageUz;
            this.messageCyrillic = messageCyrillic;
            this.messageRu = messageRu;
            this.messageEn = messageEn;
        }
    }
}
