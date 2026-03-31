package com.example.hotsearch.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    // 单例初始化时的互斥锁：用来保证多线程并发首次初始化时不会同时进入创建流程
    private static final Object LOCK = new Object();
    // AppExecutors 的单例引用：全局只保留一份线程池/执行器实例，便于统一管理
    private static volatile AppExecutors sInstance;
    // 用于磁盘 I/O 的执行器：通常执行数据库、文件读写等耗时操作，避免阻塞主线程
    private final Executor diskIO;
    // 用于主线程（UI 线程）的执行器：把任务切回主线程执行，适合更新 UI
    private final Executor mainThread;
    // 用于网络请求的执行器：并发执行网络相关任务
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        // 懒加载：第一次使用时才创建实例
        if (sInstance == null) {
            synchronized (LOCK) {
                // DCL（Double-Checked Locking）：减少锁竞争，同时保证并发初始化安全
                if (sInstance == null) {
                    sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                            // 固定大小线程池：最多同时 3 个网络任务并行执行
                            Executors.newFixedThreadPool(3),
                            // 主线程执行器：内部用 Handler 把任务 post 到主线程消息队列
                            new MainThreadExecutor());
                }
            }
        }
        return sInstance;
    }

    // 返回磁盘 I/O 执行器
    public Executor diskIO() { return diskIO; }
    // 返回主线程执行器
    public Executor mainThread() { return mainThread; }
    // 返回网络执行器
    public Executor networkIO() { return networkIO; }

    private static class MainThreadExecutor implements Executor {
        // 绑定主线程 Looper 的 Handler：保证 post 的 Runnable 在主线程执行
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            // 把任务投递到主线程消息队列，异步执行
            mainThreadHandler.post(command);
        }
    }
}
