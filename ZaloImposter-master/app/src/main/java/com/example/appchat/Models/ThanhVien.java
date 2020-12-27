package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThanhVien {
    @SerializedName("STT")
    @Expose
    private Integer sTT;
    @SerializedName("MaThanhVien")
    @Expose
    private String maThanhVien;
    @SerializedName("MaNhom")
    @Expose
    private String maNhom;
    @SerializedName("TenNhom")
    @Expose
    private String tenNhom;
    @SerializedName("TruongNhom")
    @Expose
    private Integer truongNhom;

    public Integer getsTT() {
        return sTT;
    }

    public void setsTT(Integer sTT) {
        this.sTT = sTT;
    }

    public String getMaThanhVien() {
        return maThanhVien;
    }

    public void setMaThanhVien(String maThanhVien) {
        this.maThanhVien = maThanhVien;
    }

    public String getMaNhom() {
        return maNhom;
    }

    public void setMaNhom(String maNhom) {
        this.maNhom = maNhom;
    }

    public String getTenNhom() {
        return tenNhom;
    }

    public void setTenNhom(String tenNhom) {
        this.tenNhom = tenNhom;
    }

    public Integer getTruongNhom() {
        return truongNhom;
    }

    public void setTruongNhom(Integer truongNhom) {
        this.truongNhom = truongNhom;
    }
}
