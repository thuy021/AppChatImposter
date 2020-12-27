package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemMessage {
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("userReceive")
    @Expose
    private Integer userReceive;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("type_message")
    @Expose
    private String typeMessage;
    @SerializedName("userSend")
    @Expose
    private Integer userSend;
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("nameUser")
    @Expose
    private String nameUser;

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getUserReceive() {
        return userReceive;
    }

    public void setUserReceive(Integer userReceive) {
        this.userReceive = userReceive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public Integer getUserSend() {
        return userSend;
    }

    public void setUserSend(Integer userSend) {
        this.userSend = userSend;
    }
}
