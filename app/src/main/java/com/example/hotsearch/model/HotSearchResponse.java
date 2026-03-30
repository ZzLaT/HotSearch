package com.example.hotsearch.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HotSearchResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private HotSearchData data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public HotSearchData getData() { return data; }
    public void setData(HotSearchData data) { this.data = data; }
}
