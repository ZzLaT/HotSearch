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

public class HotSearchAdapter extends ListAdapter<HotSearchItem, HotSearchAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private OnShareClickListener shareListener;

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
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getHotValue().equals(newItem.getHotValue());
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
            binding.tvIndex.setText(String.valueOf(getAdapterPosition() + 1));
            binding.tvTitle.setText(item.getTitle());
            binding.tvHot_value.setText(item.getHotValue());
            binding.tvPlatform.setText(item.getPlatform());
            // TODO: Update favorite icon based on state
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
