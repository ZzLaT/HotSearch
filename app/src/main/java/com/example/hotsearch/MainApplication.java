package com.example.hotsearch;

import android.app.Application;
import android.util.Log;

import com.example.hotsearch.utils.ShareUtils;
import com.example.hotsearch.utils.ThemeUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.applyTheme(this);

        // 简化 Logger 初始化
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }
        });

        // 测试日志输出
        Logger.d("MainApplication initialized (logger)");

        // 初始化分享 SDK
        ShareUtils.init(this);
    }
}
