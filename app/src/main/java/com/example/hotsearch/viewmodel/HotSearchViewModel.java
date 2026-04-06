package com.example.hotsearch.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.hotsearch.db.AppDatabase;
import com.example.hotsearch.model.HotSearchItem;
import com.example.hotsearch.model.Resource;
import com.example.hotsearch.repository.HotSearchRepository;
import com.example.hotsearch.utils.AppExecutors;
import java.util.List;

public class HotSearchViewModel extends AndroidViewModel {
    private final HotSearchRepository repository;
    private final MutableLiveData<String> platform = new MutableLiveData<>();
    private final LiveData<Resource<List<HotSearchItem>>> hotSearchData;
    private final LiveData<List<HotSearchItem>> allFavorites;

    public HotSearchViewModel(@NonNull Application application) {
        super(application);
        // 获取数据库单例，并初始化数据仓库 Repository
        AppDatabase db = AppDatabase.getDatabase(application);
        repository = new HotSearchRepository(db.favoriteDao(), AppExecutors.getInstance());

        // 核心逻辑：使用 switchMap 观察 platform 的变化。
        // 每当调用 setPlatform() 修改平台时，都会自动触发 repository.getHotSearch(platform) 重新获取数据。
        // 这确保了 hotSearchData 始终与当前选中的平台保持同步。
        hotSearchData = Transformations.switchMap(platform, repository::getHotSearch);
        
        // 观察所有收藏项，用于同步收藏状态
        allFavorites = repository.getAllFavorites();
    }

    public void setPlatform(String p) {
        if (p.equals(platform.getValue())) return;
        platform.setValue(p);
    }

    public LiveData<Resource<List<HotSearchItem>>> getHotSearchData() {
        return hotSearchData;
    }

    public LiveData<List<HotSearchItem>> getAllFavorites() {
        return allFavorites;
    }

    public void refresh() {
        String currentPlatform = platform.getValue();
        if (currentPlatform != null) {
            platform.setValue(currentPlatform);
        }
    }

    public void toggleFavorite(HotSearchItem item) {
        if (item.isFavorite()) {
            repository.removeFavorite(item);
            item.setFavorite(false);
        } else {
            repository.addFavorite(item);
            item.setFavorite(true);
        }
    }
}
