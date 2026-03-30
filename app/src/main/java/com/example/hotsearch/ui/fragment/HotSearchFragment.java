package com.example.hotsearch.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hotsearch.R;
import com.example.hotsearch.databinding.FragmentHotSearchBinding;
import com.example.hotsearch.model.HotSearchItem;
import com.example.hotsearch.model.Resource;
import com.example.hotsearch.ui.adapter.HotSearchAdapter;
import com.example.hotsearch.utils.NavigationUtils;
import com.example.hotsearch.utils.ShareUtils;
import com.example.hotsearch.utils.ThemeUtils;
import com.example.hotsearch.viewmodel.HotSearchViewModel;
import com.google.android.material.tabs.TabLayout;
import com.orhanobut.logger.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HotSearchFragment extends Fragment {
    private FragmentHotSearchBinding binding;
    private HotSearchViewModel viewModel;
    private HotSearchAdapter adapter;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd EEE", Locale.getDefault());

    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null) {
                Date now = new Date();
                String timeStr = timeFormat.format(now);
                String dateStr = dateFormat.format(now);
                binding.currentTime.setText(timeStr + "\n" + dateStr);
                clockHandler.postDelayed(this, 1000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d("onCreateView");
        binding = FragmentHotSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d("onViewCreated");
        viewModel = new ViewModelProvider(this).get(HotSearchViewModel.class);

        setupRecyclerView();
        setupTabs();
        setupRefresh();
        setupThemeToggle();
        observeData();

        clockHandler.post(clockRunnable);
    }

    private void setupRecyclerView() {
        adapter = new HotSearchAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Logger.d("点击条目: %s", item.getTitle());
            if (item.getUrl() == null || item.getUrl().isEmpty()) {
                Toast.makeText(getContext(), R.string.empty_message, Toast.LENGTH_SHORT).show();
            } else {
                NavigationUtils.openUrl(requireActivity(), item.getUrl());
            }
        });

        adapter.setOnFavoriteClickListener(item -> {
            Logger.d("点击收藏按钮: %s", item.getTitle());
            viewModel.toggleFavorite(item);
            Toast.makeText(getContext(), "已收藏", Toast.LENGTH_SHORT).show();
        });

        adapter.setOnShareClickListener(this::showShareDialog);
    }

    private void showShareDialog(HotSearchItem item) {
        final String[] shareOptions = {"分享到微信好友", "分享到朋友圈", "分享到QQ好友"};
        new AlertDialog.Builder(getContext())
                .setTitle("分享到...")
                .setItems(shareOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // 微信好友
                            ShareUtils.shareToWechat(getContext(), item.getUrl(), item.getTitle(), "来自「今日热搜」的分享", false);
                            break;
                        case 1: // 朋友圈
                            ShareUtils.shareToWechat(getContext(), item.getUrl(), item.getTitle(), "来自「今日热搜」的分享", true);
                            break;
                        case 2: // QQ好友
                            ShareUtils.shareToQQ(getContext(), item.getUrl(), item.getTitle(), "来自「今日热搜」的分享");
                            break;
                    }
                })
                .show();
    }

    private void setupTabs() {
        String[] platforms = {"bilibili", "weibo", "zhihu", "douyin", "kuaishou", "hupu", "toutiao", "baidu"};
        String[] platformNames = {
                getString(R.string.bilibili),
                getString(R.string.weibo),
                getString(R.string.zhihu),
                getString(R.string.douyin),
                getString(R.string.kuaishou),
                getString(R.string.hupu),
                getString(R.string.toutiao),
                getString(R.string.baidu)
        };

        for (String name : platformNames) {
            binding.platformTabs.addTab(binding.platformTabs.newTab().setText(name));
        }

        binding.platformTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String platform = platforms[tab.getPosition()];
                Logger.d("切换平台至: %s", platform);
                viewModel.setPlatform(platform);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {
                Logger.d("重复点击当前 Tab，触发刷新");
                viewModel.refresh();
            }
        });

        viewModel.setPlatform(platforms[0]);
    }

    private void setupRefresh() {
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            Logger.d("用户触发下拉刷新");
            viewModel.refresh();
        });
    }

    private void setupThemeToggle() {
        binding.themeToggle.setOnClickListener(v -> {
            Logger.d("用户点击主题切换按钮");
            ThemeUtils.toggleTheme(requireActivity());
        });
    }

    private void observeData() {
        viewModel.getHotSearchData().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    Logger.d("数据状态: LOADING");
                    binding.refreshLayout.autoRefresh();
                    break;
                case SUCCESS:
                    Logger.d("数据状态: SUCCESS, 数量: %d", (resource.data != null ? resource.data.size() : 0));
                    binding.refreshLayout.finishRefresh();
                    adapter.submitList(resource.data);
                    break;
                case ERROR:
                    Logger.e("数据状态: ERROR, 消息: %s", resource.message);
                    binding.refreshLayout.finishRefresh(false);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d("onDestroyView");
        clockHandler.removeCallbacks(clockRunnable);
        binding = null;
    }
}
