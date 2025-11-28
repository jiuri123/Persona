### 1. 重构目标
为SocialSquareFragment创建专门的SocialSquareViewModel，实现Fragment只与自己的ViewModel交互，符合单一职责原则和关注点分离原则。

### 2. 重构内容

#### 2.1 创建SocialSquareViewModel
- 文件路径：`app/src/main/java/com/example/demo/viewmodel/SocialSquareViewModel.java`
- 职责：
  - 管理社交广场的所有数据和业务逻辑
  - 聚合所需的数据源
  - 处理帖子的合并、关注/取消关注、生成新帖子等逻辑

#### 2.2 修改SocialSquareFragment
- 移除对userFollowedListViewModel、userPersonaViewModel和otherPersonaPostViewModel的直接引用
- 只与SocialSquareViewModel交互
- 简化Fragment的职责，专注于UI渲染和事件响应

#### 2.3 数据流动设计
```
SocialSquareFragment ←(观察LiveData)→ SocialSquareViewModel ←(引用)→ 各个Repository
```

### 3. 实现步骤

#### 3.1 创建SocialSquareViewModel
1. 定义SocialSquareViewModel类，继承ViewModel
2. 注入所需的Repository：
   - UserFollowedListRepository
   - UserPersonaRepository
   - OtherPersonaPostRepository
3. 定义LiveData用于暴露数据：
   - mergedPostsLiveData：合并后的帖子列表
   - isLoadingLiveData：加载状态
   - errorLiveData：错误信息
4. 实现业务逻辑方法：
   - getMergedPosts()：合并我的帖子和其他帖子
   - generateNewPost()：生成新帖子
   - onFollowClick(Persona persona)：处理关注/取消关注

#### 3.2 修改SocialSquareFragment
1. 替换所有ViewModel引用为SocialSquareViewModel
2. 更新LiveData观察逻辑，只观察SocialSquareViewModel的LiveData
3. 更新事件处理逻辑，调用SocialSquareViewModel的方法
4. 简化Fragment代码，移除业务逻辑

#### 3.3 迁移现有逻辑
1. 将SocialSquareFragment中的帖子合并逻辑迁移到SocialSquareViewModel
2. 将关注/取消关注逻辑迁移到SocialSquareViewModel
3. 将生成新帖子的逻辑迁移到SocialSquareViewModel
4. 确保所有LiveData观察和事件处理正确

### 4. 具体代码实现

#### 4.1 SocialSquareViewModel.java
```java
public class SocialSquareViewModel extends ViewModel {
    // Repository引用
    private final UserFollowedListRepository userFollowedListRepository;
    private final UserPersonaRepository userPersonaRepository;
    private final OtherPersonaPostRepository otherPersonaPostRepository;
    private final UserPersonaPostRepository userPersonaPostRepository;
    
    // LiveData定义
    private final MutableLiveData<List<Post>> mergedPostsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    // 构造函数
    public SocialSquareViewModel() {
        this.userFollowedListRepository = UserFollowedListRepository.getInstance();
        this.userPersonaRepository = UserPersonaRepository.getInstance();
        this.otherPersonaPostRepository = OtherPersonaPostRepository.getInstance();
        this.userPersonaPostRepository = UserPersonaPostRepository.getInstance();
        
        // 初始化合并帖子列表
        mergePosts();
        
        // 观察各个数据源的变化
        observeDataSources();
    }
    
    // 合并帖子逻辑
    private void mergePosts() {
        // 合并我的帖子和其他帖子
        // ...
    }
    
    // 观察数据源变化
    private void observeDataSources() {
        // 观察我的帖子变化
        // 观察其他帖子变化
        // 观察加载状态变化
        // ...
    }
    
    // 暴露给Fragment的方法
    public LiveData<List<Post>> getMergedPostsLiveData() {
        return mergedPostsLiveData;
    }
    
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }
    
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    
    public void generateNewPost() {
        // 生成新帖子逻辑
    }
    
    public void onFollowClick(Persona persona) {
        // 关注/取消关注逻辑
    }
    
    public boolean isFollowingPersona(Persona persona) {
        // 检查是否已关注
    }
    
    public LiveData<List<Persona>> getFollowedPersonasLiveData() {
        // 获取关注列表
    }
}
```

#### 4.2 修改SocialSquareFragment
```java
public class SocialSquareFragment extends Fragment implements UserFollowActionListener {
    // 只引用自己的ViewModel
    private SocialSquareViewModel socialSquareViewModel;
    
    // ... 其他代码不变
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 只初始化自己的ViewModel
        socialSquareViewModel = new ViewModelProvider(requireActivity()).get(SocialSquareViewModel.class);
        
        // 观察错误信息
        socialSquareViewModel.getErrorLiveData().observe(this, error -> {
            // 处理错误
        });
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置RecyclerView
        // ...
        
        // 观察合并后的帖子列表
        socialSquareViewModel.getMergedPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
            // 更新适配器
            socialSquarePostAdapter.updatePosts(posts);
        });
        
        // 观察加载状态
        socialSquareViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            // 更新UI
            fragmentSocialSquareBinding.fabAddPost.setEnabled(!isLoading);
        });
        
        // 观察关注列表
        socialSquareViewModel.getFollowedPersonasLiveData().observe(getViewLifecycleOwner(), followedPersonas -> {
            // 更新适配器
            socialSquarePostAdapter.updateFollowedList(followedPersonas);
        });
        
        // 设置添加帖子按钮点击事件
        fragmentSocialSquareBinding.fabAddPost.setOnClickListener(v -> {
            socialSquareViewModel.generateNewPost();
        });
    }
    
    @Override
    public void onFollowClick(Persona persona) {
        // 调用ViewModel的方法
        socialSquareViewModel.onFollowClick(persona);
    }
}
```

### 5. 预期效果
- SocialSquareFragment只与自己的ViewModel交互，职责单一
- 业务逻辑集中在ViewModel中，便于维护和测试
- 符合MVVM架构的最佳实践
- 代码结构更清晰，易于理解和扩展

### 6. 注意事项
- 确保LiveData的观察生命周期正确（使用getViewLifecycleOwner()）
- 确保Repository的单例模式正确
- 确保数据合并逻辑正确，我的帖子始终显示在前面
- 确保关注/取消关注逻辑正确，状态更新及时
- 确保生成新帖子的逻辑正确，新帖子显示在顶部