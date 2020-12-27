package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UpLoadFile {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("locationArray")
    @Expose
    private ArrayList<String> locationArray = null;
    @SerializedName("typeArray")
    @Expose
    private ArrayList<String> typeArray = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public ArrayList<String> getLocationArray() {
        return locationArray;
    }

    public void setLocationArray(ArrayList<String> locationArray) {
        this.locationArray = locationArray;
    }

    public ArrayList<String> getTypeArray() {
        return typeArray;
    }

    public void setTypeArray(ArrayList<String> typeArray) {
        this.typeArray = typeArray;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
