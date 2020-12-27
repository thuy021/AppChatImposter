package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Message {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("data")
    @Expose
    private NguoiDung data;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("action")
    @Expose
    private Integer action;
    @SerializedName("danhsach")
    @Expose
    private ArrayList<NguoiDung> danhsach;
    @SerializedName("id_room")
    @Expose
    private String id_room;

    public String getId_room() {
        return id_room;
    }

    public void setId_room(String id_room) {
        this.id_room = id_room;
    }

    public ArrayList<NguoiDung> getDanhsach() {
        return danhsach;
    }

    public void setDanhsach(ArrayList<NguoiDung> danhsach) {
        this.danhsach = danhsach;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NguoiDung getData() {
        return data;
    }

    public void setData(NguoiDung data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

}
