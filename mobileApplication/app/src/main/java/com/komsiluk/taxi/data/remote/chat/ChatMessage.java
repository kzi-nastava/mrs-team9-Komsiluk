package com.komsiluk.taxi.data.remote.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("senderId")
    @Expose
    private Long senderId;

    @SerializedName("senderEmail")
    @Expose
    private String senderEmail;

    @SerializedName("receiverId")
    @Expose
    private Long receiverId;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("sentAt")
    @Expose
    private String sentAt;

    @SerializedName("type")
    @Expose
    private String type;

    public ChatMessage() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}