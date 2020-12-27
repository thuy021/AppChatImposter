package com.example.appchat.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConversationMap {

    @SerializedName("id_user_2")
    @Expose
    private Integer idUser2;
    @SerializedName("itemLast")
    @Expose
    private Integer itemLast;
    @SerializedName("id_room")
    @Expose
    private String idRoom;
    @SerializedName("id_user_1")
    @Expose
    private Integer idUser1;

    private NguoiDung Sender;

    public Integer getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(Integer idUser2) {
        this.idUser2 = idUser2;
    }

    public Integer getItemLast() {
        return itemLast;
    }

    public void setItemLast(Integer itemLast) {
        this.itemLast = itemLast;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public Integer getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(Integer idUser1) {
        this.idUser1 = idUser1;
    }

    public NguoiDung getSender() {
        return Sender;
    }

    public void setSender(NguoiDung sender) {
        Sender = sender;
    }
}
