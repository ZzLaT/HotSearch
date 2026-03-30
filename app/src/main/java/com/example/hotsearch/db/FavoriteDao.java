package com.example.hotsearch.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.hotsearch.model.HotSearchItem;
import java.util.List;

/**
 * DAO (Data Access Object) - 数据访问对象
 * 这是一个由 Room 库管理的接口，定义了所有与 `favorite_items` 表进行交互的数据库操作。
 * 我们只需要定义方法和 SQL 注解，Room 会在编译时自动生成实现这些方法的代码。
 */
@Dao // 标记这是一个 DAO 接口
public interface FavoriteDao {
    /**
     * 插入一个收藏项。如果已存在相同主键的项，则替换它。
     * @param item 要插入的 HotSearchItem 对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // onConflict: 定义主键冲突时的策略，REPLACE 表示替换旧数据
    void insert(HotSearchItem item);

    /**
     * 删除一个收藏项。
     * @param item 要删除的 HotSearchItem 对象
     */
    @Delete
    void delete(HotSearchItem item);

    /**
     * 查询所有的收藏项，并按时间戳降序排列（最新的在最前面）。
     * 返回一个 LiveData 对象，当数据变化时，UI 可以自动收到通知并刷新。
     * @return 包含所有收藏项列表的 LiveData
     */
    @Query("SELECT * FROM favorite_items ORDER BY timestamp DESC")
    LiveData<List<HotSearchItem>> getAllFavorites();

    /**
     * 根据平台名称查询收藏项。
     * @param platform 平台标识，如 "weibo"
     * @return 该平台下的收藏项列表 LiveData
     */
    @Query("SELECT * FROM favorite_items WHERE platform = :platform ORDER BY timestamp DESC")
    LiveData<List<HotSearchItem>> getFavoritesByPlatform(String platform);

    /**
     * 根据时间范围查询收藏项。
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 该时间范围内的收藏项列表 LiveData
     */
    @Query("SELECT * FROM favorite_items WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    LiveData<List<HotSearchItem>> getFavoritesByTimeRange(long startTime, long endTime);

    /**
     * 根据平台和时间范围复合查询收藏项。
     * @param platform 平台标识
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 满足条件的收藏项列表 LiveData
     */
    @Query("SELECT * FROM favorite_items WHERE platform = :platform AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    LiveData<List<HotSearchItem>> getFavoritesByPlatformAndTimeRange(String platform, long startTime, long endTime);

    /**
     * 检查某个 URL 是否已经被收藏。
     * 使用 EXISTS 可以提高查询效率，因为它找到一个匹配项后就会立即返回，而不需要扫描整个表。
     * @param url 要检查的唯一链接
     * @return 一个布尔值的 LiveData，true 表示已收藏，false 表示未收藏
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE url = :url)")
    LiveData<Boolean> isFavorite(String url);

    /**
     * 同步检查某个 URL 是否已经被收藏。
     * @param url 要检查的唯一链接
     * @return true 表示已收藏，false 表示未收藏
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE url = :url)")
    boolean isFavoriteSync(String url);
}
