package com.example.hotsearch.api;

import com.example.hotsearch.model.HotSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface HotSearchService {
    @GET("api/v1/misc/hotboard") // 相对地址
    Call<HotSearchResponse> getHotSearch(
            @Query("type") String platform,
            @Header("Authorization") String authorization
    );
}
