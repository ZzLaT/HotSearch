package com.example.hotsearch.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HotSearchFragment extends Fragment {
    private FragmentHotSearchBinding binding;
    private HotSearchViewModel viewModel;
    private HotSearchAdapter adapter;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd EEE", Locale.getDefault());
    // 保存每个平台的滑动位置
    private final Map<String, Integer> platformScrollPositions = new HashMap<>();
    // 当前选中的平台
    private String currentPlatform;

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
        // Fragment 的视图由 FragmentManager 管理：这里使用系统传入的 inflater/container 来创建 View
        // container 是 Fragment 要挂载到的父容器；attachToRoot 传 false 表示先不直接挂载，由系统在合适时机添加
        binding = FragmentHotSearchBinding.inflate(inflater, container, false);
        Logger.d("HotSearchFragment onCreateView");
        // Fragment 通过返回根视图告诉系统“我的 UI 长什么样”，而不是像 Activity 那样 setContentView
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
            // 通知适配器数据发生了变化，更新UI
            adapter.notifyItemChanged(adapter.getCurrentList().indexOf(item));
            if (item.isFavorite()) {
                Toast.makeText(getContext(), "已收藏", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnShareClickListener(this::showShareDialog);
    }

    private void showShareDialog(HotSearchItem item) {
        final String[] shareOptions = {"微信好友", "朋友圈"};
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
                
                // 保存当前平台的滑动位置
                if (currentPlatform != null) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int scrollPosition = layoutManager.findFirstVisibleItemPosition();
                        platformScrollPositions.put(currentPlatform, scrollPosition);
                    }
                }
                
                // 切换平台
                currentPlatform = platform;
                viewModel.setPlatform(platform);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {
                Logger.d("重复点击当前 Tab，触发刷新");
                viewModel.refresh();
            }
        });

        // 初始化当前平台
        currentPlatform = platforms[0];
        viewModel.setPlatform(currentPlatform);
    }

    private void setupRefresh() {
        binding.refreshLayout.setOnRefreshListener(() -> {
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
                    binding.refreshLayout.setRefreshing(true);
                    break;
                case SUCCESS:
                    Logger.d("数据状态: SUCCESS, 数量: %d", (resource.data != null ? resource.data.size() : 0));
                    binding.refreshLayout.setRefreshing(false);
                    
                    final long renderStartTime = System.currentTimeMillis();
                    final boolean hasScrollPosition = currentPlatform != null
                            && platformScrollPositions.containsKey(currentPlatform);
                    final int scrollPosition = hasScrollPosition
                            ? platformScrollPositions.get(currentPlatform) : 0;
                    //submitList(...) 是 AndroidX RecyclerView 的 ListAdapter （以及内部用到的 AsyncListDiffer ）提供的方法 ，
                    // 用来把“新的列表数据”提交给 Adapter，让它自动计算差异并刷新 RecyclerView。
                    adapter.submitList(resource.data);
                    
                    binding.recyclerView.post(() -> {
                        binding.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                binding.recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                if (hasScrollPosition) {
                                    LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                                    if (layoutManager != null) {
                                        layoutManager.scrollToPositionWithOffset(scrollPosition, 0);
                                    }
                                }
                                long renderDuration = System.currentTimeMillis() - renderStartTime;
                                Logger.i("列表渲染完成, 耗时: %dms, 恢复位置: %s%d",
                                        renderDuration, hasScrollPosition ? "item" : "无", scrollPosition);
                            }
                        });
                    });
                    break;
                case ERROR:
                    Logger.e("数据状态: ERROR, 消息: %s", resource.message);
                    binding.refreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // 观察收藏列表变化，同步更新首页的收藏状态
        viewModel.getAllFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites == null) return;
            
            // 创建收藏 URL 的 Set，用于快速查找
            java.util.Set<String> favoriteUrls = new java.util.HashSet<>();
            for (HotSearchItem item : favorites) {
                favoriteUrls.add(item.getUrl());
            }
            
            // 更新当前列表中每个 item 的收藏状态
            java.util.List<HotSearchItem> currentList = adapter.getCurrentList();
            boolean hasChanges = false;
            for (int i = 0; i < currentList.size(); i++) {
                HotSearchItem item = currentList.get(i);
                boolean isFavorite = favoriteUrls.contains(item.getUrl());
                if (item.isFavorite() != isFavorite) {
                    item.setFavorite(isFavorite);
                    adapter.notifyItemChanged(i);
                    hasChanges = true;
                }
            }
            
            if (hasChanges) {
                Logger.d("收藏状态已同步更新");
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
