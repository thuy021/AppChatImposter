package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BanBe implements Serializable {
    @SerializedName("MaNguoiDung_Mot")
    @Expose
    private int MaNguoiDung_Mot;
    @SerializedName("MaNguoiDung_Hai")
    @Expose
    private int MaNguoiDung_Hai;
    @SerializedName("TrangThai")
    @Expose
    private int TrangThai;
    @SerializedName("HoatDong")
    @Expose
    private int HoatDong;
    public int getMaNguoiDung_Mot() {
        return MaNguoiDung_Mot;
    }

    public void setMaNguoiDung_Mot(int maNguoiDung_Mot) {
        MaNguoiDung_Mot = maNguoiDung_Mot;
    }

    public int getMaNguoiDung_Hai() {
        return MaNguoiDung_Hai;
    }

    public void setMaNguoiDung_Hai(int maNguoiDung_Hai) {
        MaNguoiDung_Hai = maNguoiDung_Hai;
    }

    public int getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(int trangThai) {
        TrangThai = trangThai;
    }

    public int getHoatDong() {
        return HoatDong;
    }

    public void setHoatDong(int hoatDong) {
        HoatDong = hoatDong;
    }
}
