## 修改计划

### 目标
将 `SocialSquareFragment` 对 `OnFollowClickListener` 接口的实现方式从类级实现改为在调用方法时传递匿名内部类实现，保持接口定义在 `SocialSquarePostAdapter` 内部不变。

### 修改内容

1. **修改 SocialSquareFragment 类声明**
   - 移除 `implements SocialSquarePostAdapter.OnFollowClickListener` 接口实现

2. **修改 setOnFollowActionListener 调用方式**
   - 将 `socialSquarePostAdapter.setOnFollowActionListener(this)` 改为传递匿名内部类或 lambda 表达式
   - 在匿名内部类中实现 `onFollowClick` 方法，直接调用 `socialSquareViewModel.onFollowClick(persona)`

3. **删除独立的 onFollowClick 方法**
   - 移除 `SocialSquareFragment` 中独立的 `onFollowClick` 方法实现

### 修改后的代码结构

```java
// 移除接口实现
public class SocialSquareFragment extends Fragment {
    // ...
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // ...
        
        // 创建适配器并设置回调接口（使用匿名内部类）
        socialSquarePostAdapter = new SocialSquarePostAdapter(getContext(), postList);
        socialSquarePostAdapter.setOnFollowActionListener(new SocialSquarePostAdapter.OnFollowClickListener() {
            @Override
            public void onFollowClick(Persona persona) {
                socialSquareViewModel.onFollowClick(persona);
            }
        });
        fragmentSocialSquareBinding.rvSocialSquare.setAdapter(socialSquarePostAdapter);
        
        // ...
    }
    
    // 移除独立的 onFollowClick 方法
    
    // ...
}
```

### 预期效果
- 保持接口定义在 `SocialSquarePostAdapter` 内部不变
- 减少 `SocialSquareFragment` 的接口实现，使类结构更清晰
- 接口实现更加内聚，只在需要的地方定义
- 符合单一职责原则，提高代码可维护性

### 影响范围
- 仅影响 `SocialSquareFragment.java` 文件
- 不影响其他类和功能
- 保持原有功能不变

### 实施步骤
1. 打开 `SocialSquareFragment.java` 文件
2. 修改类声明，移除接口实现
3. 修改 `onViewCreated` 方法中的 `setOnFollowActionListener` 调用
4. 删除独立的 `onFollowClick` 方法
5. 编译并测试确保功能正常