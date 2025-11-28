## 问题分析
当前三个Fragment对应的ViewModel都存在直接暴露Repository的LiveData的问题，违反了MVVM架构的关注点分离原则：

1. **SocialSquareViewModel**：`getFollowedPersonasLiveData()`直接返回Repository的LiveData
2. **UserFollowedListViewModel**：`getFollowedPersonas()`直接返回Repository的LiveData
3. **UserPersonaCreateAndChatViewModel**：所有getter方法都直接返回Repository的LiveData

## 解决方案
使用MediatorLiveData重构这三个ViewModel，实现View只观察ViewModel，ViewModel观察Repository的真正MVVM架构。

## 修改步骤

### 1. 重构 `SocialSquareViewModel.java`
- 添加 `MediatorLiveData<List<Persona>>` 用于包装关注列表
- 在构造函数中设置Repository观察者
- 修改 `getFollowedPersonasLiveData()` 方法返回MediatorLiveData

### 2. 重构 `UserFollowedListViewModel.java`
- 添加 `MediatorLiveData<List<Persona>>` 用于包装关注列表
- 在构造函数中设置Repository观察者
- 修改 `getFollowedPersonas()` 方法返回MediatorLiveData

### 3. 重构 `UserPersonaCreateAndChatViewModel.java`
- 添加多个 `MediatorLiveData` 用于包装所有Repository的LiveData
- 在构造函数中设置Repository观察者
- 修改所有getter方法返回MediatorLiveData

## 预期效果
- 符合MVVM架构原则，View只与ViewModel交互
- ViewModel成为真正的数据中转层，可添加业务逻辑处理
- 使用MediatorLiveData更优雅地处理LiveData源的观察
- 关注点分离更清晰，便于后续扩展和维护

## 代码修改示例

### 1. `SocialSquareViewModel.java` 修改点
- 添加 `MediatorLiveData<List<Persona>> followedPersonasLiveData`
- 在构造函数中调用 `setupMediatorLiveData()`
- 实现 `setupMediatorLiveData()` 方法，添加Repository LiveData为源
- 修改 `getFollowedPersonasLiveData()` 返回 `followedPersonasLiveData`

### 2. `UserFollowedListViewModel.java` 修改点
- 添加 `MediatorLiveData<List<Persona>> followedPersonasLiveData`
- 在构造函数中调用 `setupMediatorLiveData()`
- 实现 `setupMediatorLiveData()` 方法，添加Repository LiveData为源
- 修改 `getFollowedPersonas()` 返回 `followedPersonasLiveData`

### 3. `UserPersonaCreateAndChatViewModel.java` 修改点
- 添加多个 `MediatorLiveData` 实例
- 在构造函数中调用 `setupMediatorLiveData()`
- 实现 `setupMediatorLiveData()` 方法，添加所有Repository LiveData为源
- 修改所有getter方法返回对应的MediatorLiveData

## 预期结果
通过这次修改，我们将实现三个ViewModel的真正MVVM架构，其中：
- View 只观察 ViewModel
- ViewModel 观察 Repository
- 各层职责清晰，符合关注点分离原则
- 代码结构更易于维护和扩展