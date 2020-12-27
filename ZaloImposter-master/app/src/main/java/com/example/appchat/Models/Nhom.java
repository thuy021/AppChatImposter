package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nhom {

    @SerializedName("MaNhom")
    @Expose
    private String MaNhom;
    @SerializedName("TenNhom")
    @Expose
    private String TenNhom;
    @SerializedName("TruongNhom")
    @Expose
    private int TruongNhom;

    public String getMaNhom() {
        return MaNhom;
    }

    public void setMaNhom(String maNhom) {
        MaNhom = maNhom;
    }

    public String getTenNhom() {
        return TenNhom;
    }

    public void setTenNhom(String tenNhom) {
        TenNhom = tenNhom;
    }

    public int getTruongNhom() {
        return TruongNhom;
    }

    public void setTruongNhom(int truongNhom) {
        TruongNhom = truongNhom;
    }
}
