package uz.zafar.onlineshoptelegrambot.dto;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public class TelegramUpdateData {

    private Long chatId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private Integer messageId;
    private Long contactUserId;
    /* ===== MESSAGE CONTENT ===== */
    private String text;          // TEXT
    private String callbackData;  // CALLBACK
    private String phoneNumber;   // CONTACT
    private Double latitude;      // LOCATION
    private Double longitude;// LOCATION
    private CallbackQuery callbackQuery;
    private String callbackQueryId;
    private MessageType type;

    private List<String> photoFileIds;
    private String videoFileId;
    private String documentFIleId;

    public enum MessageType {
        TEXT,
        CALLBACK,
        CONTACT,
        LOCATION,
        PHOTO,
        VIDEO,
        DOCUMENT,
        UNKNOWN
    }

    /* ===== GETTERS / SETTERS ===== */

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public MessageType getType() {
        return type;
    }

    public CallbackQuery getCallbackQuery() {
        return callbackQuery;
    }

    public void setCallbackQuery(CallbackQuery callbackQuery) {
        this.callbackQuery = callbackQuery;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public List<String> getPhotoFileIds() {
        return photoFileIds;
    }

    public void setPhotoFileIds(List<String> photoFileIds) {
        this.photoFileIds = photoFileIds;
    }

    public String getVideoFileId() {
        return videoFileId;
    }

    public void setVideoFileId(String videoFileId) {
        this.videoFileId = videoFileId;
    }

    public String getDocumentFIleId() {
        return documentFIleId;
    }

    public void setDocumentFIleId(String documentFIleId) {
        this.documentFIleId = documentFIleId;
    }

    public String getCallbackQueryId() {
        return callbackQueryId;
    }

    public void setCallbackQueryId(String callbackQueryId) {
        this.callbackQueryId = callbackQueryId;
    }
}
