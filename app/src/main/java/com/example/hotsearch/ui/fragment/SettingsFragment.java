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
    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d("onCreateView");
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d("onViewCreated");

        boolean isDarkMode = ThemeUtils.isDarkMode(requireActivity());
        Logger.d("当前主题模式: %s", isDarkMode ? "深色" : "浅色");
        binding.switchDarkMode.setChecked(isDarkMode);
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Logger.d("切换主题模式: %s", isChecked ? "深色" : "浅色");
            ThemeUtils.setDarkMode(requireActivity(), isChecked);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d("onDestroyView");
        binding = null;
    }
}
