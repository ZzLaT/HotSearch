package com.example.hotsearch.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.hotsearch.db.AppDatabase;
import com.example.hotsearch.model.HotSearchItem;
import com.example.hotsearch.repository.HotSearchRepository;
import com.example.hotsearch.utils.AppExecutors;
import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {
    private final HotSearchRepository repository;
    private final MutableLiveData<String> platformFilter = new MutableLiveData<>("all");

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        repository = new HotSearchRepository(db.favoriteDao(), AppExecutors.getInstance());
    }

    public LiveData<List<HotSearchItem>> getFavorites() {
        return Transformations.switchMap(platformFilter, p -> {
            if ("all".equals(p)) {
                return repository.getAllFavorites();
            } else {
                return repository.getFavoritesByPlatform(p);
            }
        });
    }

    public void setPlatformFilter(String platform) {
        platformFilter.setValue(platform);
    }

    public void removeFavorite(HotSearchItem item) {
        repository.removeFavorite(item);
    }
}
