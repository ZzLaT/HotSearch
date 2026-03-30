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

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private FavoriteViewModel viewModel;
    private HotSearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            NavigationUtils.openUrl(requireActivity(), item.getUrl());
        });

        adapter.setOnFavoriteClickListener(item -> {
            viewModel.removeFavorite(item);
        });
    }

    private void setupFilters() {
        binding.chipGroupPlatform.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == com.example.hotsearch.R.id.chip_all) {
                viewModel.setPlatformFilter("all");
            } else if (checkedId == com.example.hotsearch.R.id.chip_weibo) {
                viewModel.setPlatformFilter("weibo");
            } else if (checkedId == com.example.hotsearch.R.id.chip_zhihu) {
                viewModel.setPlatformFilter("zhihu");
            } else if (checkedId == com.example.hotsearch.R.id.chip_bilibili) {
                viewModel.setPlatformFilter("bilibili");
            } else if (checkedId == com.example.hotsearch.R.id.chip_douyin) {
                viewModel.setPlatformFilter("douyin");
            } else if (checkedId == com.example.hotsearch.R.id.chip_kuaishou) {
                viewModel.setPlatformFilter("kuaishou");
            } else if (checkedId == com.example.hotsearch.R.id.chip_hupu) {
                viewModel.setPlatformFilter("hupu");
            } else if (checkedId == com.example.hotsearch.R.id.chip_toutiao) {
                viewModel.setPlatformFilter("toutiao");
            } else if (checkedId == com.example.hotsearch.R.id.chip_baidu) {
                viewModel.setPlatformFilter("baidu");
            }
        });
    }

    private void observeFavorites() {
        viewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            adapter.submitList(favorites);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
