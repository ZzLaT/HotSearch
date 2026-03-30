package com.example.hotsearch;

import android.app.Application;
import com.example.hotsearch.utils.ThemeUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.applyTheme(this);

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("HOT_SEARCH")      // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        // 初始化分享 SDK
        ShareUtils.init(this);
    }
}
