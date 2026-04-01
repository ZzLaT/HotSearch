package com.example.hotsearch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hotsearch.databinding.FragmentFavoriteBinding;
import com.example.hotsearch.ui.adapter.HotSearchAdapter;
import com.example.hotsearch.utils.NavigationUtils;
import com.example.hotsearch.viewmodel.FavoriteViewModel;
import com.orhanobut.logger.Logger;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private FavoriteViewModel viewModel;
    private HotSearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d("onCreateView");
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d("onViewCreated");
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        setupRecyclerView();
        setupFilters();
        observeFavorites();
    }

    private void setupRecyclerView() {
        adapter = new HotSearchAdapter();
        adapter.setFavoritePage(true); // 设置为收藏页面
        binding.recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewFavorite.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Logger.d("点击收藏条目: %s", item.getTitle());
            NavigationUtils.openUrl(requireActivity(), item.getUrl());
        });

        adapter.setOnFavoriteClickListener(item -> {
            Logger.d("取消收藏: %s", item.getTitle());
            viewModel.removeFavorite(item);
        });
    }

    private void setupFilters() {
        binding.chipGroupPlatform.setOnCheckedChangeListener((group, checkedId) -> {
            String platform = "all";
            if (checkedId == com.example.hotsearch.R.id.chip_all) {
                platform = "all";
            } else if (checkedId == com.example.hotsearch.R.id.chip_weibo) {
                platform = "weibo";
            } else if (checkedId == com.example.hotsearch.R.id.chip_zhihu) {
                platform = "zhihu";
            } else if (checkedId == com.example.hotsearch.R.id.chip_bilibili) {
                platform = "bilibili";
            } else if (checkedId == com.example.hotsearch.R.id.chip_douyin) {
                platform = "douyin";
            } else if (checkedId == com.example.hotsearch.R.id.chip_kuaishou) {
                platform = "kuaishou";
            } else if (checkedId == com.example.hotsearch.R.id.chip_hupu) {
                platform = "hupu";
            } else if (checkedId == com.example.hotsearch.R.id.chip_toutiao) {
                platform = "toutiao";
            } else if (checkedId == com.example.hotsearch.R.id.chip_baidu) {
                platform = "baidu";
            }
            Logger.d("切换收藏过滤平台: %s", platform);
            viewModel.setPlatformFilter(platform);
        });
    }

    private void observeFavorites() {
        viewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            Logger.d("收藏数据更新: 数量=%d", (favorites != null ? favorites.size() : 0));
            // ListAdapter 提供的方法，用来把“新的数据列表”交给 Adapter：
            // 内部会用 DiffUtil.ItemCallback 对比旧列表和新列表，计算哪些 item 变了/新增/删除
            // 然后只对变化的部分触发 RecyclerView 刷新
            adapter.submitList(favorites);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d("onDestroyView");
        binding = null;
    }
}
