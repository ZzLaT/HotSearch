package com.example.hotsearch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.orhanobut.logger.Logger;

public class ThemeUtils {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    public static void setDarkMode(Activity activity, boolean isDark) {
        if (activity != null) {
            long start = System.currentTimeMillis();
            SharedPreferences.Editor editor = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(KEY_DARK_MODE, isDark);
            editor.apply();

            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            activity.recreate();
            long duration = System.currentTimeMillis() - start;
            Logger.d("主题切换耗时: %dms, 模式: %s", duration, isDark ? "深色" : "浅色");
        }
    }

    public static boolean isDarkMode(Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return prefs.getBoolean(KEY_DARK_MODE, false);
        }
        return false;
    }

    public static void applyTheme(Context context) {
        if (isDarkMode(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void toggleTheme(Activity activity) {
        if (activity != null) {
            setDarkMode(activity, !isDarkMode(activity));
        }
    }
}
