# 热搜应用 Bug 修复记录

## 问题 1：收藏状态在切换 Tab 后不保持

### 问题描述
当用户在某个平台（如B站）点击收藏图标后，切换到其他Tab（如微博），然后再切回B站，刚刚收藏的那条item的收藏图标并不会显示高亮。

### 原因分析
当切换Tab时，会重新调用`getHotSearch`方法获取数据，而在这个方法中，我们通过`dao.isFavorite(item.getUrl())`检查收藏状态，但是由于这是一个`LiveData`，在主线程中直接调用`getValue()`可能会返回null，因为数据还没有从数据库加载完成。

### 解决方案
修改`HotSearchRepository.java`文件中的`getHotSearch`方法，使用同步的方式检查收藏状态。我们可以创建一个同步的方法来检查收藏状态，而不是使用`LiveData`。

### 代码修改
1. 在`FavoriteDao.java`中添加一个同步的方法来检查收藏状态：
```java
/**
 * 同步检查某个 URL 是否已经被收藏。
 * @param url 要检查的唯一链接
 * @return true 表示已收藏，false 表示未收藏
 */
@Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE url = :url)")
boolean isFavoriteSync(String url);
```

2. 在`HotSearchRepository.java`中修改`getHotSearch`方法，使用同步的方式检查收藏状态：
```java
// 为每个 item 标记平台，使用接口返回的 actualType，便于后续入库/过滤
for (HotSearchItem item : items) {
    item.setPlatform(actualType);
    // 检查是否已经被收藏（使用同步方法）
    boolean isFavorite = dao.isFavoriteSync(item.getUrl());
    item.setFavorite(isFavorite);
}
```

## 问题 2：滑动位置在切换 Tab 后不保存

### 问题描述
在B站滑动到某个位置的时候，切换到微博再切回b站时，b站的滑动位置并没有保存下来。

### 原因分析
当切换Tab时，会重新创建RecyclerView的Adapter并提交新的数据，导致RecyclerView的滑动位置重置。

### 解决方案
为每个平台保存对应的滑动位置，当切换回该平台时，恢复之前的滑动位置。

### 代码修改
1. 在`HotSearchFragment.java`中添加一个`Map`来保存每个平台的滑动位置：
```java
// 保存每个平台的滑动位置
private final Map<String, Integer> platformScrollPositions = new HashMap<>();
// 当前选中的平台
private String currentPlatform;
```

2. 修改`setupTabs`方法，在切换Tab时保存和恢复滑动位置：
```java
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
```

3. 修改`observeData`方法，在数据加载完成后恢复滑动位置：
```java
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
                adapter.submitList(resource.data);
                
                // 恢复滑动位置
                if (currentPlatform != null && platformScrollPositions.containsKey(currentPlatform)) {
                    int scrollPosition = platformScrollPositions.get(currentPlatform);
                    binding.recyclerView.scrollToPosition(scrollPosition);
                }
                break;
            case ERROR:
                Logger.e("数据状态: ERROR, 消息: %s", resource.message);
                binding.refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                break;
        }
    });
}
```

4. 初始化`currentPlatform`：
```java
// 初始化当前平台
currentPlatform = platforms[0];
viewModel.setPlatform(currentPlatform);
```

## 问题 3：收藏页面中热搜编号混乱

### 问题描述
在收藏页面中，"全部"的热搜编号会混乱。

### 原因分析
在`HotSearchAdapter.java`的`bind`方法中，我们使用`getAdapterPosition() + 1`作为热搜编号，但是在收藏页面中，这个位置是基于收藏列表的顺序，而不是原始的热搜排名。

### 解决方案
修改`HotSearchAdapter.java`，让它能够区分是在首页显示还是在收藏页面显示，在收藏页面中不显示编号，或者使用其他方式显示编号。

### 代码修改
1. 在`HotSearchAdapter.java`中添加一个标志来表示是否在收藏页面显示：
```java
private boolean isFavoritePage = false;

public void setFavoritePage(boolean favoritePage) {
    isFavoritePage = favoritePage;
}
```

2. 修改`bind`方法，根据是否在收藏页面来决定是否显示编号：
```java
void bind(HotSearchItem item) {
    if (!isFavoritePage) {
        binding.tvIndex.setText(String.valueOf(getAdapterPosition() + 1));
    } else {
        binding.tvIndex.setText(""); // 在收藏页面不显示编号
    }
    binding.tvTitle.setText(item.getTitle());
    // 只显示热度值，不显示平台名称
    binding.tvHotValue.setText(item.getHotValue());
    // Update favorite icon based on state
    if (item.isFavorite()) {
        binding.btnFavorite.setImageResource(R.drawable.ic_star_filled);
    } else {
        binding.btnFavorite.setImageResource(R.drawable.ic_star_outline);
    }
}
```

3. 在`FavoriteFragment.java`中设置`isFavoritePage`标志：
```java
private void setupRecyclerView() {
    adapter = new HotSearchAdapter();
    adapter.setFavoritePage(true); // 设置为收藏页面
    binding.recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.recyclerViewFavorite.setAdapter(adapter);
    
    // 其他代码...
}
```

## 总结

通过以上修改，我们解决了三个问题：
1. 收藏状态在切换Tab后不保持 - 通过使用同步的方式检查收藏状态
2. 滑动位置在切换Tab后不保存 - 通过为每个平台保存滑动位置
3. 收藏页面中热搜编号混乱 - 通过在收藏页面不显示编号

这些修改提高了应用的用户体验，让用户在使用过程中更加顺畅。