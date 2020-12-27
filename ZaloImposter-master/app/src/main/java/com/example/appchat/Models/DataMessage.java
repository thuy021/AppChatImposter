package com.example.appchat.Models;

import android.content.ClipData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataMessage {
    @SerializedName("Items")
    @Expose
    private ArrayList<ItemMessage> items;
    @SerializedName("Count")
    @Expose
    private Integer count;
    @SerializedName("ScannedCount")
    @Expose
    private Integer scannedCount;
    @SerializedName("LastEvaluatedKey")
    @Expose
    private LastEvaluatedKey lastEvaluatedKey;

    public ArrayList<ItemMessage> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemMessage> items) {
        this.items = items;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getScannedCount() {
        return scannedCount;
    }

    public void setScannedCount(Integer scannedCount) {
        this.scannedCount = scannedCount;
    }

    public LastEvaluatedKey getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

    public void setLastEvaluatedKey(LastEvaluatedKey lastEvaluatedKey) {
        this.lastEvaluatedKey = lastEvaluatedKey;
    }
}
