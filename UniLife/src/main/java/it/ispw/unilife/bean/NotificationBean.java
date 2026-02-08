package it.ispw.unilife.bean;

import java.time.LocalDateTime;

public class NotificationBean {
    private String senderUsername;
    private String message;
    private LocalDateTime timestamp;
    private String status;
    private String notificationType; // "RESERVATION", "LESSON", "COURSE", "APPLICATION"

    public NotificationBean() {
    }

    public NotificationBean(String senderUsername, String message, LocalDateTime timestamp, String status, String notificationType) {
        this.senderUsername = senderUsername;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.notificationType = notificationType;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}