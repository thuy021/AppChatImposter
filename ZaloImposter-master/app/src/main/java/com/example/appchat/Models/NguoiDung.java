package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class NguoiDung implements Serializable {
    @SerializedName("MaNguoiDung")
    @Expose
    private int maNguoiDung;
    @SerializedName("HoTen")
    @Expose
    private String hoTen;
    @SerializedName("SoDienThoai")
    @Expose
    private String soDienThoai;
    @SerializedName("NgaySinh")
    @Expose
    private String ngaySinh;
    @SerializedName("GioiTinh")
    @Expose
    private Boolean gioiTinh;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("Status")
    @Expose
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public Boolean getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(Boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NguoiDung nguoiDung = (NguoiDung) o;
        return maNguoiDung == nguoiDung.maNguoiDung;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNguoiDung);
    }
}
