package com.example.hotsearch.utils;

import com.example.hotsearch.R;
import java.util.HashMap;
import java.util.Map;

public class PlatformIconUtil {
    private static final Map<String, Integer> PLATFORM_ICONS = new HashMap<>();
    private static final Map<String, String> PLATFORM_NAMES = new HashMap<>();

    static {
        PLATFORM_ICONS.put("bilibili", R.drawable.ic_platform_bilibili);
        PLATFORM_ICONS.put("weibo", R.drawable.ic_platform_weibo);
        PLATFORM_ICONS.put("zhihu", R.drawable.ic_platform_zhihu);
        PLATFORM_ICONS.put("douyin", R.drawable.ic_platform_douyin);
        PLATFORM_ICONS.put("kuaishou", R.drawable.ic_platform_kuaishou);
        PLATFORM_ICONS.put("hupu", R.drawable.ic_platform_hupu);
        PLATFORM_ICONS.put("toutiao", R.drawable.ic_platform_toutiao);
        PLATFORM_ICONS.put("baidu", R.drawable.ic_platform_baidu);

        PLATFORM_NAMES.put("bilibili", "哔哩哔哩");
        PLATFORM_NAMES.put("weibo", "微博");
        PLATFORM_NAMES.put("zhihu", "知乎");
        PLATFORM_NAMES.put("douyin", "抖音");
        PLATFORM_NAMES.put("kuaishou", "快手");
        PLATFORM_NAMES.put("hupu", "虎扑");
        PLATFORM_NAMES.put("toutiao", "头条");
        PLATFORM_NAMES.put("baidu", "百度");
    }

    public static int getPlatformIconResId(String platform) {
        return PLATFORM_ICONS.getOrDefault(platform, R.drawable.ic_platform_unknown);
    }

    public static String getPlatformName(String platform) {
        return PLATFORM_NAMES.getOrDefault(platform, platform);
    }
}
