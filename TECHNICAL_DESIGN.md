# 今日热搜 App 技术开发文档

## 1. 项目概述
“今日热搜”是一款聚合类 Android 应用，旨在为用户提供各大主流平台（如微博、知乎、百度、抖音等）的实时热搜榜单。用户可以方便地查看不同平台的热点话题，支持收藏、渠道切换及跳转至原平台查看。

---

## 2. 技术架构与栈

### 2.1 核心技术栈
- **开发语言**: Java (必须使用)
- **UI 布局**: XML (View-based)
- **网络请求**: Retrofit 2 + OkHttp 3 + Gson
- **数据持久化**: Room Database + SharedPreferences
- **刷新组件**: SmartRefreshLayout (高级下拉刷新/上拉加载)
- **列表展示**: RecyclerView (配合 DiffUtil 局部刷新)
- **浏览器跳转**: Android Intent / Chrome Custom Tabs

### 2.2 架构模式 (MVVM)
- **Model**: 定义实体类（[HotSearchItem.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/model/HotSearchItem.java)）及数据接口。
- **View**: Activity/Fragment（[MainActivity.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/ui/MainActivity.java)）及对应 XML。
- **ViewModel**: 处理 UI 业务逻辑，通过 `LiveData` 与 View 通信。
- **Repository**: 数据仓库层，封装网络请求（Retrofit）与本地存储（Room）的调度逻辑。

---

## 3. UI/UX 交互设计

### 3.1 导航架构
- **底部主导航**: 使用 `TabLayout` + `ViewPager2` 实现多页面平滑切换。
- **页面组成**:
    - **首页 (HotSearchFragment)**: 实时热搜聚合。
    - **收藏 (FavoriteFragment)**: 本地持久化收藏列表。
    - **设置 (SettingsFragment)**: 平台定制及主题管理。

### 3.2 首页 Header UI 设计
- **视觉元素**:
    - **Logo**: 红色圆形背景 + 白色火焰矢量图标。
    - **标题**: 主标题“今日热榜”（加粗），副标题“汇聚全网热点...”（灰色小字）。
    - **时间/日历**: 右侧展示实时时间（精确到秒）及对应的农历/星期信息。
- **交互功能**:
    - **深色模式切换**: 右上角提供月亮/太阳图标按钮，支持手动切换主题。

### 3.3 渠道切换逻辑
- **二级导航**: Header 下方提供横向滑动的平台切换栏（HorizontalScrollView + Chip/TabLayout）。
- **动态联动**: 仅展示用户在设置中开启的平台，点击即时刷新当前列表数据。

---

## 4. 业务逻辑设计

### 4.1 UI 状态管理 (UI State Management)
- **设计思路**: 使用封装类（如 `Resource<T>`）来统一管理数据的加载状态：
    - **Loading**: 显示加载进度条。
    - **Success**: 成功获取数据并刷新列表。
    - **Error**: 捕获异常，展示错误信息及重试按钮。
- **UI 响应**: View 通过观察 `LiveData<Resource<List<HotSearchItem>>>` 自动切换界面状态，确保主线程只负责渲染，不负责逻辑判断。

### 4.2 列表按需加载 (Lazy Loading)
- **策略**: 初始仅加载首屏可见条目。
- **实现**: 结合 `SmartRefreshLayout` 的上拉加载功能，当用户滑动到底部时动态请求并追加后续数据，优化内存占用。

### 4.3 浏览器跳转与 App 唤起策略 (稳妥方案)
- **校验流程**: 点击列表条目时，首先检查 `url` 字段。
- **跳转优先级**:
    1. **URL 为空**: 弹出 `Toast` 提示：“该话题暂无详情链接，请稍后再试”。
    2. **URL 非空 - 尝试唤起 App**: 使用 `Intent.ACTION_VIEW` 发起请求。若系统已关联对应 App（如微博、知乎），则引导用户直接在对应 App 中打开。
    3. **应用内展示 (Chrome Custom Tabs)**: 若未安装对应 App，优先调用 `Chrome Custom Tabs` 在应用内快速渲染网页，保证用户体验的一致性且不跳出 App。
    4. **系统浏览器兜底**: 若设备不支持 Custom Tabs，则自动回退至系统默认浏览器打开链接。

### 4.4 收藏管理
- **存储机制**: 使用 Room 数据库（[AppDatabase.java](file:///c:/Users/Lenovo/HotSearch/db/AppDatabase.java)）进行本地持久化。
- **默认展示**: 收藏页面初始按 **收藏时间倒序**（最新收藏在前）展示。
- **多维筛选功能**:
    - **渠道筛选**: 支持按来源平台（如微博、知乎等）过滤收藏条目。
    - **时间范围筛选**: 支持用户选择起始和结束日期，筛选该时段内的收藏记录。
    - **复合筛选**: 支持同时应用“渠道”和“时间范围”过滤条件。
- **数据同步**: 列表项支持一键收藏/取消，UI 状态通过 LiveData 与数据库保持实时同步。

---

## 5. 并发处理与线程管理

### 5.1 异步网络请求
- **Retrofit 异步调度**: 使用 Retrofit 的 `enqueue()` 方法发起异步请求，避免在主线程执行网络操作导致 ANR。
- **OkHttp 缓存机制**: 配置 OkHttp 的 `Cache` 拦截器，在离线或弱网环境下优先展示缓存数据。

### 5.2 数据库异步操作
- **Executor 线程池**: 在 Java 环境下，Room 的增删改查操作必须在非主线程执行。通过定义全局 `AppExecutors` 管理后台任务。
- **复杂查询支持**: [FavoriteDao.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/db/FavoriteDao.java) 需支持动态 SQL 或多个查询方法，以满足渠道、时间范围及复合条件的筛选需求。
- **LiveData 自动调度**: Room 返回的 `LiveData` 会自动在后台线程执行查询，并在主线程分发结果，简化代码实现。

---

## 6. 数据层设计

### 6.1 接口协议 (JSON)
使用 [HotSearchResponse.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/model/HotSearchResponse.java) 进行解析。示例结构：
```json
{
  "type": "weibo",
  "update_time": "2026-03-30 09:30:26",
  "list": [
    {
      "index": 1,
      "title": "广州暴雨",
      "url": "https://s.weibo.com/...",
      "hot_value": "1132964"
    }
  ]
}
```

### 6.2 数据库设计 (Room)
**表名: `favorite_items`**
| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 主键 (自增) |
| index | Integer | 原始排名 |
| title | String | 话题标题 |
| url | String | 跳转链接 (可为空) |
| hot_value | String | 热度值 |
| platform | String | 来源平台 |
| timestamp | Long | 收藏时间戳 |

---

## 7. 异常处理与性能优化

- **网络容错**: 请求失败时展示 Empty View 占位图，支持点击屏幕重试。
- **列表性能优化**: 
    - **DiffUtil**: 计算列表差异，避免无效的全量刷新，提升滑动流畅度。
    - **ViewBinding**: (推荐) 使用 ViewBinding 替代 `findViewById`，提升 View 查找效率并保证类型安全。
    - **RecyclerView 优化**: 设置 `setHasFixedSize(true)` 并配置合理的缓存大小（`setItemViewCacheSize`）。
- **主题适配**: 
    - **手动切换**: 通过 `AppCompatDelegate.setDefaultNightMode()` 实现。
    - **持久化**: 记录用户偏好至 `SharedPreferences`。
- **权限管理**: [AndroidManifest.xml](file:///c:/Users/Lenovo/HotSearch/app/src/main/AndroidManifest.xml) 中声明 `INTERNET` 权限。

---

## 8. 模块化划分
- `ui`: 视图层 (Activity, Fragment, Adapter)
- `viewmodel`: 业务逻辑与数据持有，使用 `ViewModelProvider` 管理。
- `model`: 实体类 (API Response & Database Entity)
- `api`: Retrofit 接口定义 ([HotSearchService.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/api/HotSearchService.java))
- `db`: Room 数据库组件
- `repository`: 数据仓库，统一管理内存缓存、数据库和网络数据流。

---

## 9. 第三方服务集成 (待实现)

### 9.1 分享功能 (Share)

- **技术栈选型**: 
    - **核心**: 采用**微信开放平台**和**腾讯 QQ 互联**的官方 SDK，以实现功能丰富、体验流畅的定向分享。
    - **兜底**: 使用 Android 原生的 `Intent.ACTION_SEND` 作为补充，允许用户分享到其他支持的 App。

- **实现流程**:
    1.  **依赖集成**: 在 `build.gradle.kts` 中添加微信和 QQ 的 SDK 依赖。
    2.  **AppID 注册**: 在微信和 QQ 的开发者平台注册应用，获取 `AppID` 并配置到项目中。
    3.  **创建分享入口**: 在热搜列表项的长按菜单或详情页中，增加“分享”按钮，点击后弹出包含“微信好友”、“朋友圈”、“QQ 好友”等图标的分享面板。
    4.  **API 调用**: 
        - 封装一个 `ShareUtils` 工具类，根据用户选择的渠道（微信/QQ），构造对应的分享请求对象（如 `SendMessageToWX.Req`）。
        - 请求对象中包含分享类型（网页）、标题、描述、缩略图和跳转链接。
        - 调用相应 SDK 的 `sendReq` 方法拉起微信/QQ 的分享界面。
    5.  **回调处理**: 在指定的 `WXEntryActivity` 或 `WXPayEntryActivity` 中处理来自微信的分享结果回调（成功、取消、失败）。QQ 的回调则通过 `IUiListener` 接口处理。
    6.  **权限与配置**: 在 `AndroidManifest.xml` 中声明必要的 `<queries>` 标签，以确保在 Android 11+ 系统上能正常唤起微信和 QQ。

---

## 10. 扩展功能规划 (待实现)
- **跨平台搜索**: 聚合全网关键词检索。
- **桌面 Widget**: 实时掌握 Top 3 爆点话题。
- **消息推送**: 突发新闻爆点提醒。
