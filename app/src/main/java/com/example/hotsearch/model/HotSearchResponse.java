package com.example.hotsearch.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HotSearchResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("update_time")
    private String updateTime;

    @SerializedName("list")
    private List<HotSearchItem> list;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public List<HotSearchItem> getList() { return list; }
    public void setList(List<HotSearchItem> list) { this.list = list; }
}
