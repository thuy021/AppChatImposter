package com.example.appchat.Models;

import java.io.Serializable;

public class NhanTin implements Serializable {
    private String tinGui;
    private String tinNhan;
    private String type_message;
    private Boolean base64;
    private String urlFile;
    private String tenNhan;

    public String getTenNhan() {
        return tenNhan;
    }

    public void setTenNhan(String tenNhan) {
        this.tenNhan = tenNhan;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public Boolean getBase64() {
        return base64;
    }

    public void setBase64(Boolean base64) {
        this.base64 = base64;
    }

    public String getType_message() {
        return type_message;
    }

    public void setType_message(String type_message) {
        this.type_message = type_message;
    }

    public String getTinGui() {
        return tinGui;
    }

    public void setTinGui(String tinGui) {
        this.tinGui = tinGui;
    }

    public String getTinNhan() {
        return tinNhan;
    }

    public void setTinNhan(String tinNhan) {
        this.tinNhan = tinNhan;
    }

    public NhanTin() {

    }

    public NhanTin(String tinGui, String tinNhan, String type_message, Boolean base64, String urlFile, String tenNhan) {
        this.tinGui = tinGui;
        this.tinNhan = tinNhan;
        this.type_message = type_message;
        this.base64 = base64;
        this.urlFile = urlFile;
        this.tenNhan = tenNhan;
    }
}
