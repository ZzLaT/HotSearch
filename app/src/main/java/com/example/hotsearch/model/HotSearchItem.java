package com.example.hotsearch.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * 热搜条目实体类
 * 既是 Retrofit 解析网络数据时使用的模型，也是 Room 数据库中存储收藏项的表结构。
 */
@Entity(tableName = "favorite_items") // Room 注解，标记这是一个数据库实体，并指定表名为 favorite_items
public class HotSearchItem {
    @PrimaryKey(autoGenerate = true) // Room 注解，标记 id 为主键，并设置其自增长
    private int id; // 数据库中的唯一 ID
    // @SerializedName 是 Gson 库提供的一个核心注解，
    // 它的作用就像一个“ 贴标签 ”的工具，专门用来解决 JSON 字段名 和 Java 变量名 不一致的问题。
    @SerializedName("index") // Gson 注解，将 JSON 中的 index 字段映射到此变量
    private int index; // 热搜榜单排名

    @SerializedName("title")
    private String title; // 标题

    @SerializedName("url")
    private String url; // 详情页链接

    @SerializedName("hot_value") // 根据 B 站示例，对应 "hot_value"
    private String hotValue; // 热度值

    // 对应接口外层的 "type" 字段，在 Repository 中手动赋值
    private String platform; // 平台来源 (如: bilibili, weibo)
    private long timestamp; // 收藏时的时间戳，用于排序

    public HotSearchItem() {
        // Room 需要一个无参构造函数
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getHotValue() { return hotValue; }
    public void setHotValue(String hotValue) { this.hotValue = hotValue; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
