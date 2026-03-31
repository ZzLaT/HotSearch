package com.example.hotsearch.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.hotsearch.model.HotSearchItem;

/**
 * Room 数据库的主类，作为数据库的持有者和主访问点。
 * 使用单例模式确保在整个应用中只有一个数据库实例。
 */
@Database(entities = {HotSearchItem.class}, version = 3, exportSchema = false)
// entities: 定义数据库包含哪些表 (实体类)
// version: 数据库版本号，用于数据库升级
// exportSchema: 是否导出数据库结构到 JSON 文件，此处禁用
public abstract class AppDatabase extends RoomDatabase {
    // 使用 volatile 保证多线程环境下的可见性
    private static volatile AppDatabase INSTANCE;

    // Room 会自动实现这个抽象方法，返回 FavoriteDao 的实例
    public abstract FavoriteDao favoriteDao();

    /**
     * 获取数据库的单例实例。
     * 采用双重检查锁定（Double-Checked Locking）确保线程安全。
     * @param context 上下文
     * @return AppDatabase 的单例实例
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) { // 第一次检查，避免不必要的同步开销
            synchronized (AppDatabase.class) { // 同步块，保证线程安全
                if (INSTANCE == null) { // 第二次检查，防止多个线程同时进入同步块并创建多个实例
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "hotsearch_database") // 数据库文件名
                            .fallbackToDestructiveMigration() // 允许破坏性迁移，当数据库架构变化时删除旧数据库并创建新数据库
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
