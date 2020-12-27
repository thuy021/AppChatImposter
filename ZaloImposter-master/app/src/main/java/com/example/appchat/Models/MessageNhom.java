package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MessageNhom {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("MaNhom")
    @Expose
    private String maNhom;
    @SerializedName("data")
    @Expose
    private ArrayList<ThanhVien> data = null;
    @SerializedName("dataGroup")
    @Expose
    private ArrayList<Nhom> dataGroup = null;

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

    public String getMaNhom() {
        return maNhom;
    }

    public void setMaNhom(String maNhom) {
        this.maNhom = maNhom;
    }

    public ArrayList<ThanhVien> getData() {
        return data;
    }

    public void setData(ArrayList<ThanhVien> data) {
        this.data = data;
    }

    public ArrayList<Nhom> getDataGroup() {
        return dataGroup;
    }

    public void setDataGroup(ArrayList<Nhom> dataGroup) {
        this.dataGroup = dataGroup;
    }
}
