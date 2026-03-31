package com.example.hotsearch.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotsearch.R;
import com.example.hotsearch.databinding.ItemHotSearchBinding;
import com.example.hotsearch.model.HotSearchItem;
import com.example.hotsearch.utils.PlatformIconUtil;
import com.orhanobut.logger.Logger;

public class HotSearchAdapter extends ListAdapter<HotSearchItem, HotSearchAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private OnShareClickListener shareListener;
    private boolean isFavoritePage = false;

    public void setFavoritePage(boolean favoritePage) {
        isFavoritePage = favoritePage;
    }

    public interface OnItemClickListener {
        void onItemClick(HotSearchItem item);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(HotSearchItem item);
    }

    public interface OnShareClickListener {
        void onShareClick(HotSearchItem item);
    }

    public HotSearchAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<HotSearchItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<HotSearchItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull HotSearchItem oldItem, @NonNull HotSearchItem newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HotSearchItem oldItem, @NonNull HotSearchItem newItem) {
            String oldPlatform = oldItem.getPlatform() != null ? oldItem.getPlatform() : "";
            String newPlatform = newItem.getPlatform() != null ? newItem.getPlatform() : "";
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getHotValue().equals(newItem.getHotValue()) &&
                    oldItem.isFavorite() == newItem.isFavorite() &&
                    oldPlatform.equals(newPlatform);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHotSearchBinding binding = ItemHotSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotSearchItem item = getItem(position);
        holder.bind(item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHotSearchBinding binding;

        ViewHolder(ItemHotSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(pos));
                }
            });

            binding.btnFavorite.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (favoriteListener != null && pos != RecyclerView.NO_POSITION) {
                    favoriteListener.onFavoriteClick(getItem(pos));
                }
            });

            binding.btnShare.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (shareListener != null && pos != RecyclerView.NO_POSITION) {
                    shareListener.onShareClick(getItem(pos));
                }
            });
        }

        void bind(HotSearchItem item) {
            Logger.d("绑定数据: %s, 平台: %s, 收藏状态: %b", item.getTitle(), item.getPlatform(), item.isFavorite());
            
            if (!isFavoritePage) {
                binding.tvIndex.setText(String.valueOf(getAdapterPosition() + 1));
                binding.ivPlatformIcon.setVisibility(View.GONE);
            } else {
                binding.tvIndex.setText("");
                binding.ivPlatformIcon.setVisibility(View.VISIBLE);
                
                String platform = item.getPlatform();
                if (platform == null || platform.isEmpty()) {
                    platform = inferPlatformFromUrl(item.getUrl());
                    Logger.d("从URL推断平台: %s -> %s", item.getUrl(), platform);
                }
                
                int iconResId = PlatformIconUtil.getPlatformIconResId(platform);
                Logger.d("加载平台图标: %s -> %d", platform, iconResId);
                binding.ivPlatformIcon.setImageResource(iconResId);
            }
            binding.tvTitle.setText(item.getTitle());
            binding.tvHotValue.setText(item.getHotValue());
            if (item.isFavorite()) {
                binding.btnFavorite.setImageResource(R.drawable.ic_star_filled);
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_star_outline);
            }
        }

        private String inferPlatformFromUrl(String url) {
            if (url == null) return "unknown";
            if (url.contains("bilibili.com") || url.contains("b23.tv")) return "bilibili";
            if (url.contains("weibo.com") || url.contains("weibo.cn")) return "weibo";
            if (url.contains("zhihu.com")) return "zhihu";
            if (url.contains("douyin.com") || url.contains("iesdouyin.com")) return "douyin";
            if (url.contains("kuaishou.com")) return "kuaishou";
            if (url.contains("hupu.com")) return "hupu";
            if (url.contains("toutiao.com")) return "toutiao";
            if (url.contains("baidu.com")) return "baidu";
            return "unknown";
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteListener = listener;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        this.shareListener = listener;
    }
}
