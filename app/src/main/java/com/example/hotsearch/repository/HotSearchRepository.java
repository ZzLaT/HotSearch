package com.example.hotsearch.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hotsearch.api.HotSearchService;
import com.example.hotsearch.db.FavoriteDao;
import com.example.hotsearch.model.HotSearchItem;
import com.example.hotsearch.model.HotSearchResponse;
import com.example.hotsearch.model.Resource;
import com.example.hotsearch.utils.AppExecutors;
import com.orhanobut.logger.Logger;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HotSearchRepository {
    private final HotSearchService service;
    private final FavoriteDao dao;
    private final AppExecutors executors;
    private static final String API_KEY = "af2per5a****er5a"; // Replace with your actual key

    public HotSearchRepository(FavoriteDao dao, AppExecutors executors) {
        this.dao = dao; // 注入本地数据库 DAO，用于收藏数据的增删查
        this.executors = executors; // 注入线程调度器，确保数据库/IO 不阻塞主线程

        // 配置 Retrofit 客户端：设置基础域名与 JSON 解析器
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://uapis.cn/") // 聚合热榜 API 基地址
                .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析 JSON
                .build();
        // 生成 API 接口实例，用于后续发起网络请求
        this.service = retrofit.create(HotSearchService.class);
    }

    // 拉取指定平台的热搜数据，并以 Resource 封装加载/成功/错误状态暴露给上层
    public LiveData<Resource<List<HotSearchItem>>> getHotSearch(String platform) {
        // 可观察数据容器：ViewModel/Fragment 通过观察它来接收状态变更
        MutableLiveData<Resource<List<HotSearchItem>>> result = new MutableLiveData<>();
        // 首先发出 Loading 状态，便于 UI 展示加载进度
        result.setValue(Resource.loading(null));

        // 性能埋点：记录请求耗时
        final long startTime = System.currentTimeMillis();
        Logger.d("开始获取热搜, 平台: %s", platform);

        // 发起 Retrofit 异步请求，避免阻塞主线程
        service.getHotSearch(platform, API_KEY).enqueue(new Callback<HotSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<HotSearchResponse> call, @NonNull Response<HotSearchResponse> response) {
                // 计算耗时
                long duration = System.currentTimeMillis() - startTime;
                // 先判断 HTTP 层是否成功，且有响应体
                if (response.isSuccessful() && response.body() != null) {
                    HotSearchResponse body = response.body();
                    // 再判断业务层 code 与 data 是否有效
                    if (body.getCode() == 200 && body.getData() != null) {
                        List<HotSearchItem> items = body.getData().getList();
                        String actualType = body.getData().getType(); // 获取接口返回的真实平台 type
                        Logger.i("获取成功! 平台: %s (接口返回: %s), 耗时: %dms, 条数: %d", platform, actualType, duration, items.size());
                        
                        // 为每个 item 标记平台，使用接口返回的 actualType，便于后续入库/过滤
                        for (HotSearchItem item : items) {
                            item.setPlatform(actualType);
                        }
                        // 以 Success 状态返回数据
                        result.setValue(Resource.success(items));
                    } else {
                        // 业务码或数据异常，返回 Error 状态
                        Logger.e("API 业务错误! 平台: %s, Code: %d, Msg: %s", platform, body.getCode(), body.getMsg());
                        result.setValue(Resource.error("数据异常: " + body.getMsg(), null));
                    }
                } else {
                    // HTTP 层失败（如 4xx/5xx），返回 Error 状态
                    Logger.e("HTTP 请求失败! 平台: %s, Status: %d, Msg: %s", platform, response.code(), response.message());
                    result.setValue(Resource.error("请求失败: " + response.message(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<HotSearchResponse> call, @NonNull Throwable t) {
                // 网络异常（如超时/无网络），返回 Error 状态
                long duration = System.currentTimeMillis() - startTime;
                Logger.e(t, "网络请求异常! 平台: %s, 耗时: %dms", platform, duration);
                result.setValue(Resource.error("网络异常: " + t.getLocalizedMessage(), null));
            }
        });

        // 返回可观察的数据容器，UI 层通过观察它来获取状态更新
        return result;
    }

    public LiveData<List<HotSearchItem>> getAllFavorites() {
        return dao.getAllFavorites();
    }

    public LiveData<List<HotSearchItem>> getFavoritesByPlatform(String platform) {
        return dao.getFavoritesByPlatform(platform);
    }

    public LiveData<List<HotSearchItem>> getFavoritesByTimeRange(long start, long end) {
        return dao.getFavoritesByTimeRange(start, end);
    }

    public void addFavorite(HotSearchItem item) {
        Logger.d("尝试添加收藏: %s", item.getTitle());
        executors.diskIO().execute(() -> {
            try {
                item.setTimestamp(System.currentTimeMillis());
                dao.insert(item);
                Logger.i("收藏成功: %s", item.getTitle());
            } catch (Exception e) {
                Logger.e(e, "收藏失败: %s", item.getTitle());
            }
        });
    }

    public void removeFavorite(HotSearchItem item) {
        Logger.d("尝试取消收藏: %s", item.getTitle());
        executors.diskIO().execute(() -> {
            try {
                dao.delete(item);
                Logger.i("取消收藏成功: %s", item.getTitle());
            } catch (Exception e) {
                Logger.e(e, "取消收藏失败: %s", item.getTitle());
            }
        });
    }

    public LiveData<Boolean> isFavorite(String url) {
        return dao.isFavorite(url);
    }
}
