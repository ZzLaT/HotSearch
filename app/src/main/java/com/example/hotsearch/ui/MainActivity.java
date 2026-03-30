package com.example.hotsearch.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.hotsearch.databinding.ActivityMainBinding;
import com.example.hotsearch.ui.fragment.FavoriteFragment;
import com.example.hotsearch.ui.fragment.HotSearchFragment;
import com.example.hotsearch.ui.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // 视图绑定对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化 ViewBinding 并设置内容视图
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 测试日志输出
        com.orhanobut.logger.Logger.d("MainActivity onCreate");

        // 配置 ViewPager2 适配器，用于切换首页、收藏、设置页面
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new HotSearchFragment();
                    case 1: return new FavoriteFragment();
                    case 2: return new SettingsFragment();
                    default: return new HotSearchFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3; // 页面总数
            }
        });

        // 底部导航栏点击事件：控制 ViewPager2 切换到对应页面
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == com.example.hotsearch.R.id.nav_home) {
                binding.viewPager.setCurrentItem(0, false); // false 表示禁用平滑滚动
                return true;
            } else if (itemId == com.example.hotsearch.R.id.nav_favorite) {
                binding.viewPager.setCurrentItem(1, false);
                return true;
            } else if (itemId == com.example.hotsearch.R.id.nav_settings) {
                binding.viewPager.setCurrentItem(2, false);
                return true;
            }
            return false;
        });

        // 禁用 ViewPager2 的左右手势滑动切换，统一通过底部导航栏控制
        binding.viewPager.setUserInputEnabled(false);
    }
}
