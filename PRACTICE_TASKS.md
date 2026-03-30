# 🛠 “今日热搜” App 动手实战任务书

> **任务说明**: 请按照任务描述进行代码修改，并使用 `.\gradlew assembleDebug` (或 Android Studio 的 Run 按钮) 验证改动效果。

---

## 1. Model 层：扩展数据模型 (Difficulty: ⭐)
- **定位路径**: [HotSearchItem.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/model/HotSearchItem.java)
- **任务描述**: 
    1. 在 `HotSearchItem` 类中新增一个 `String description` 字段。
    2. 生成对应的 Getter 和 Setter 方法。
    3. 在 `HotSearchAdapter` 中尝试将该字段显示在列表项中（可选）。
- **提交物**: 修改后的 `HotSearchItem.java`。
- **验证**: 编译通过且无报错。

## 2. UI 层：美化列表项 (Difficulty: ⭐⭐)
- **定位路径**: [item_hot_search.xml](file:///c:/Users/Lenovo/HotSearch/app/src/main/res/layout/item_hot_search.xml)
- **任务描述**: 
    1. 将热度值 `tv_hot_value` 的文字颜色改为红色 (`@color/red`)。
    2. 给 `MaterialCardView` 增加一个 1dp 的描边 (`app:strokeWidth="1dp"` 和 `app:strokeColor="#DDDDDD"`)。
- **提交物**: 修改后的 `item_hot_search.xml`。
- **验证**: 运行 App，查看列表项是否有描边效果。

## 3. ViewModel 层：增加排序逻辑 (Difficulty: ⭐⭐⭐)
- **定位路径**: [HotSearchViewModel.java](file:///c:/Users/Lenovo/HotSearch/app/src/main/java/com/example/hotsearch/viewmodel/HotSearchViewModel.java)
- **任务描述**: 
    1. 在 `HotSearchViewModel` 中，当收到成功的数据后，尝试对列表按热度值 (`hotValue`) 进行倒序排列。
    2. 提示：热度值目前是字符串，可能需要先转换为数字。
- **提交物**: 修改后的 `HotSearchViewModel.java`。
- **验证**: 运行 App，检查热搜列表是否按热度从高到低排列。

---

## 📅 自动化测试验证 (针对进阶用户)
如果你已经安装了测试框架，可以运行以下命令验证核心逻辑：
```bash
./gradlew testDebugUnitTest
```
该命令会运行 `com.example.hotsearch` 包下的所有单元测试。
