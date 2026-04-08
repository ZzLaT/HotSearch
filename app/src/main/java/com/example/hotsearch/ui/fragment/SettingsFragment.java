package com.example.hotsearch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.hotsearch.databinding.FragmentSettingsBinding;
import com.example.hotsearch.utils.ThemeUtils;
import com.orhanobut.logger.Logger;

public class SettingsFragment extends Fragment {
    private long fragmentCreateStartTime;
    private long fragmentViewCreatedTime;
    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCreateStartTime = System.currentTimeMillis();
        Logger.d("SettingsFragment onCreateView 开始");
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        long createDuration = System.currentTimeMillis() - fragmentCreateStartTime;
        Logger.d("SettingsFragment onCreateView 完成，耗时: %dms", createDuration);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentViewCreatedTime = System.currentTimeMillis();
        long viewCreatedDuration = fragmentViewCreatedTime - fragmentCreateStartTime;
        Logger.d("SettingsFragment onViewCreated 开始，从onCreateView到现在耗时: %dms", viewCreatedDuration);

        boolean isDarkMode = ThemeUtils.isDarkMode(requireActivity());
        Logger.d("当前主题模式: %s", isDarkMode ? "深色" : "浅色");
        binding.switchDarkMode.setChecked(isDarkMode);
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Logger.d("切换主题模式: %s", isChecked ? "深色" : "浅色");
            ThemeUtils.setDarkMode(requireActivity(), isChecked);
        });
        Logger.d("SettingsFragment onViewCreated 完成");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        long fragmentLifetime = System.currentTimeMillis() - fragmentCreateStartTime;
        Logger.d("SettingsFragment onDestroyView，生命周期耗时: %dms", fragmentLifetime);
        binding = null;
    }
}
