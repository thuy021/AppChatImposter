package com.example.appchat.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Room implements Serializable {

    @SerializedName("id_room")
    @Expose
    private String id_room;
    @SerializedName("id_user_1")
    @Expose
    private int id_user_1;
    @SerializedName("id_user_2")
    @Expose
    private int id_user_2;

    public int getItemLast() {
        return itemLast;
    }

    public void setItemLast(int itemLast) {
        this.itemLast = itemLast;
    }

    @SerializedName("itemLast")
    @Expose
    private int itemLast;

    public String getId_room() {
        return id_room;
    }

    public void setId_room(String id_room) {
        this.id_room = id_room;
    }

    public int getId_user_1() {
        return id_user_1;
    }

    public void setId_user_1(int id_user_1) {
        this.id_user_1 = id_user_1;
    }

    public int getId_user_2() {
        return id_user_2;
    }

    public void setId_user_2(int id_user_2) {
        this.id_user_2 = id_user_2;
    }
}
