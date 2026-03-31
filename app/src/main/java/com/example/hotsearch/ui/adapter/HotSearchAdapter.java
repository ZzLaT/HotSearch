package com.example.hotsearch.ui.adapter;

// 负责 item view 的创建/绑定所需的 Android 视图相关类
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// AndroidX 注解与 RecyclerView/Adapter 相关类
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

// 本项目资源、ViewBinding、数据模型、工具与日志
import com.example.hotsearch.R;
import com.example.hotsearch.databinding.ItemHotSearchBinding;
import com.example.hotsearch.model.HotSearchItem;
import com.example.hotsearch.utils.PlatformIconUtil;
import com.orhanobut.logger.Logger;

/**
 * 热搜列表 Adapter：
 * - 继承 ListAdapter：内置异步 diff（配合 DIFF_CALLBACK）与 submitList 更新机制
 * - ViewHolder 使用 ViewBinding（ItemHotSearchBinding）进行视图绑定
 */
public class HotSearchAdapter extends ListAdapter<HotSearchItem, HotSearchAdapter.ViewHolder> {

    // 三类回调：点击整行、点击收藏、点击分享
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private OnShareClickListener shareListener;

    // 标记当前是否是“收藏页”，用于控制 UI 展示（排名/平台图标等）
    private boolean isFavoritePage = false;

    // 外部可设置当前页面模式
    public void setFavoritePage(boolean favoritePage) {
        isFavoritePage = favoritePage;
    }

    // item 点击回调接口
    public interface OnItemClickListener {
        void onItemClick(HotSearchItem item);
    }

    // 收藏按钮点击回调接口
    public interface OnFavoriteClickListener {
        void onFavoriteClick(HotSearchItem item);
    }

    // 分享按钮点击回调接口
    public interface OnShareClickListener {
        void onShareClick(HotSearchItem item);
    }

    // 构造：把 DiffUtil 回调交给 ListAdapter，用于计算列表差异并触发局部刷新/动画
    public HotSearchAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * DiffUtil 回调：
     * - areItemsTheSame：判断“是不是同一个条目”（一般用唯一 id/url）
     * - areContentsTheSame：判断“内容是否变化”（决定是否需要重新 bind）
     *
     * 注意：这里把 url 作为唯一标识；如果 url 可能为空或不唯一，会影响 diff 正确性。
     */
    private static final DiffUtil.ItemCallback<HotSearchItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HotSearchItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull HotSearchItem oldItem, @NonNull HotSearchItem newItem) {
            // 同一条数据：用 url 判断
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HotSearchItem oldItem, @NonNull HotSearchItem newItem) {
            // 平台字段做空值兜底，避免 null.equals 崩溃
            String oldPlatform = oldItem.getPlatform() != null ? oldItem.getPlatform() : "";
            String newPlatform = newItem.getPlatform() != null ? newItem.getPlatform() : "";

            // 内容对比：标题、热度值、收藏状态、平台
            // 注意：如果 HotSearchItem 还有其他影响 UI 的字段没比较，会导致 UI 不刷新。
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getHotValue().equals(newItem.getHotValue()) &&
                    oldItem.isFavorite() == newItem.isFavorite() &&
                    oldPlatform.equals(newPlatform);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用 ViewBinding inflate item 布局（ItemHotSearchBinding 对应 item_hot_search.xml）
        ItemHotSearchBinding binding = ItemHotSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // ListAdapter 通过 getItem(position) 拿到当前项数据
        HotSearchItem item = getItem(position);
        holder.bind(item);
    }

    /**
     * ViewHolder：
     * - 持有 binding，负责给 itemView 设置点击事件与数据绑定
     * - 当前实现：在构造函数里设置点击事件；bind 里只做 UI 赋值
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHotSearchBinding binding;

        ViewHolder(ItemHotSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // 点击整行：取 position -> 回调 item
            // 注意：getAdapterPosition 在某些时机可能返回 NO_POSITION（比如动画/刷新期间）
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(pos));
                }
            });

            // 点击收藏按钮：回调当前 item
            binding.btnFavorite.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (favoriteListener != null && pos != RecyclerView.NO_POSITION) {
                    favoriteListener.onFavoriteClick(getItem(pos));
                }
            });

            // 点击分享按钮：回调当前 item
            binding.btnShare.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (shareListener != null && pos != RecyclerView.NO_POSITION) {
                    shareListener.onShareClick(getItem(pos));
                }
            });
        }

        /**
         * 绑定数据到视图：
         * - 收藏页：显示平台 icon，不显示排名
         * - 非收藏页：显示排名，不显示平台 icon
         */
        void bind(HotSearchItem item) {
            // 调试日志：打印标题、平台、收藏状态，便于定位绑定/复用问题
            Logger.d("绑定数据: %s, 平台: %s, 收藏状态: %b", item.getTitle(), item.getPlatform(), item.isFavorite());

            if (!isFavoritePage) {
                // 非收藏页：显示序号（这里用 getAdapterPosition() + 1）
                // 注意：bind 阶段用 getAdapterPosition 可能受刷新/动画影响；更稳的是用 onBind 的 position 或 bindingAdapterPosition。
                binding.tvIndex.setText(String.valueOf(getAdapterPosition() + 1));
                binding.ivPlatformIcon.setVisibility(View.GONE);
            } else {
                // 收藏页：不显示序号，显示平台图标
                binding.tvIndex.setText("");
                binding.ivPlatformIcon.setVisibility(View.VISIBLE);

                // 优先使用 item 自带平台字段；为空则通过 url 推断
                String platform = item.getPlatform();
                if (platform == null || platform.isEmpty()) {
                    platform = inferPlatformFromUrl(item.getUrl());
                    Logger.d("从URL推断平台: %s -> %s", item.getUrl(), platform);
                }

                // 根据平台字符串映射到资源 id，然后设置到 ImageView
                int iconResId = PlatformIconUtil.getPlatformIconResId(platform);
                Logger.d("加载平台图标: %s -> %d", platform, iconResId);
                binding.ivPlatformIcon.setImageResource(iconResId);
            }

            // 通用字段绑定：标题与热度
            binding.tvTitle.setText(item.getTitle());
            binding.tvHotValue.setText(item.getHotValue());

            // 收藏状态切换图标
            if (item.isFavorite()) {
                binding.btnFavorite.setImageResource(R.drawable.ic_star_filled);
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_star_outline);
            }
        }

        /**
         * 根据 URL 粗略推断平台：
         * - 适用于平台字段缺失时兜底
         * - 返回值用于 PlatformIconUtil 映射图标
         */
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

    // 对外暴露设置回调
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