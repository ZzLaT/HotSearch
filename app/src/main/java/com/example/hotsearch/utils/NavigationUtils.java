package com.example.hotsearch.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;

public class NavigationUtils {
    public static void openUrl(Activity activity, String url) {
        if (url == null || url.isEmpty()) return;

        Uri uri = Uri.parse(url);

        // 1. 尝试直接通过 Intent.ACTION_VIEW 唤起应用
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 注意：在 Android 11+ 中，需要 query 权限或特定配置才能检测。
            // 这里我们采用 try-catch 方式直接启动。
            try {
                activity.startActivity(intent);
                return;
            } catch (Exception ignored) {}
        }

        // 2. 尝试 Chrome Custom Tabs
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            customTabsIntent.launchUrl(activity, uri);
        } catch (Exception e) {
            // 3. 兜底方案：系统浏览器
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(browserIntent);
        }
    }
}
