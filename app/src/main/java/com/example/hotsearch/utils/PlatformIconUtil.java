package com.example.hotsearch.utils;

import com.example.hotsearch.R;
import java.util.HashMap;
import java.util.Map;

public class PlatformIconUtil {
    private static final Map<String, Integer> PLATFORM_ICONS = new HashMap<>();
    private static final Map<String, String> PLATFORM_NAMES = new HashMap<>();
    private static final Map<String, String> PLATFORM_ICON_URLS = new HashMap<>();

    static {
        // 本地图标资源（作为备用）
        PLATFORM_ICONS.put("bilibili", R.drawable.ic_platform_bilibili);
        PLATFORM_ICONS.put("weibo", R.drawable.ic_platform_weibo);
        PLATFORM_ICONS.put("zhihu", R.drawable.ic_platform_zhihu);
        PLATFORM_ICONS.put("douyin", R.drawable.ic_platform_douyin);
        PLATFORM_ICONS.put("kuaishou", R.drawable.ic_platform_kuaishou);
        PLATFORM_ICONS.put("hupu", R.drawable.ic_platform_hupu);
        PLATFORM_ICONS.put("toutiao", R.drawable.ic_platform_toutiao);
        PLATFORM_ICONS.put("baidu", R.drawable.ic_platform_baidu);

        // 平台名称
        PLATFORM_NAMES.put("bilibili", "哔哩哔哩");
        PLATFORM_NAMES.put("weibo", "微博");
        PLATFORM_NAMES.put("zhihu", "知乎");
        PLATFORM_NAMES.put("douyin", "抖音");
        PLATFORM_NAMES.put("kuaishou", "快手");
        PLATFORM_NAMES.put("hupu", "虎扑");
        PLATFORM_NAMES.put("toutiao", "头条");
        PLATFORM_NAMES.put("baidu", "百度");

        PLATFORM_ICON_URLS.put("bilibili", "https://www.bilibili.com/favicon.ico");
        PLATFORM_ICON_URLS.put("weibo", "https://www.weibo.com/favicon.ico");
        PLATFORM_ICON_URLS.put("zhihu", "https://www.zhihu.com/favicon.ico");
        PLATFORM_ICON_URLS.put("douyin", "https://www.douyin.com/favicon.ico");
        // 快手的favicon.ico返回405错误，使用本地图标作为主要方案
        // 国内无法访问Google Favicon API，保留本地图标作为备用
        PLATFORM_ICON_URLS.put("kuaishou", "https://www.kuaishou.com/favicon.ico");
        PLATFORM_ICON_URLS.put("hupu", "https://www.hupu.com/favicon.ico");
        PLATFORM_ICON_URLS.put("toutiao", "https://www.toutiao.com/favicon.ico");
        PLATFORM_ICON_URLS.put("baidu", "https://www.baidu.com/favicon.ico");
    }

    public static int getPlatformIconResId(String platform) {
        return PLATFORM_ICONS.getOrDefault(platform, R.drawable.ic_platform_unknown);
    }

    public static String getPlatformName(String platform) {
        return PLATFORM_NAMES.getOrDefault(platform, platform);
    }

    public static String getPlatformIconUrl(String platform) {
        return PLATFORM_ICON_URLS.getOrDefault(platform, null);
    }
}
