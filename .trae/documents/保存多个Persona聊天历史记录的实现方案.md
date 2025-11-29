# 保存多个Persona聊天历史记录的实现方案

## 问题分析
当前`OtherPersonaChatRepository`只能保存一个Persona的聊天历史，切换Persona时会清除之前的聊天记录。需要修改设计以支持保存多个Persona的聊天历史。

## 解决方案
使用Map结构来存储不同Persona的聊天历史和API请求历史，以Persona名称作为唯一标识（假设名称唯一）。

## 实现步骤

### 1. 修改`OtherPersonaChatRepository`类

#### 1.1 替换单个聊天历史为Map结构
- 将`chatHistoryLiveData`保持为单个LiveData，用于UI观察当前Persona的聊天记录
- 添加`chatHistoryMap`：`Map<String, List<ChatMessage>>`，用于存储所有Persona的聊天历史
- 添加`apiHistoryMap`：`Map<String, List<ChatRequestMessage>>`，用于存储所有Persona的API请求历史

#### 1.2 修改构造函数
- 初始化`chatHistoryMap`和`apiHistoryMap`

#### 1.3 修改`setCurrentPersona`方法
- 当设置新的Persona时，从Map中获取该Persona的聊天历史（如果不存在则创建新列表）
- 更新`chatHistoryLiveData`的值为当前Persona的聊天历史

#### 1.4 修改`sendMessage`方法
- 移除初始化空聊天历史的代码
- 从`chatHistoryMap`获取当前Persona的聊天历史
- 从`apiHistoryMap`获取当前Persona的API请求历史
- 发送消息后，将更新后的聊天历史和API历史保存回Map

#### 1.5 修改`handleApiError`方法
- 确保错误消息被添加到当前Persona的聊天历史中

### 2. 关键代码修改

```java
// 替换原有的单个聊天历史和API历史
private final MutableLiveData<List<ChatMessage>> chatHistoryLiveData;
private final Map<String, List<ChatMessage>> chatHistoryMap;
private final Map<String, List<ChatRequestMessage>> apiHistoryMap;

// 构造函数
public OtherPersonaChatRepository() {
    this.apiService = ApiClient.getApiService();
    this.chatHistoryLiveData = new MutableLiveData<>();
    this.chatHistoryMap = new HashMap<>();
    this.apiHistoryMap = new HashMap<>();
}

// setCurrentPersona方法
public void setCurrentPersona(Persona persona) {
    this.currentPersona = persona;
    // 获取或创建该Persona的聊天历史
    List<ChatMessage> personaChatHistory = chatHistoryMap.computeIfAbsent(persona.getName(), k -> new ArrayList<>());
    chatHistoryLiveData.setValue(personaChatHistory);
}

// sendMessage方法
public void sendMessage(String userMessageText) {
    // 构建系统提示
    String systemPrompt = "你现在扮演 " + currentPersona.getName() + "。" +
            "你的背景故事是：" + currentPersona.getBackgroundStory() + "。" +
            "你的个性签名是：" + currentPersona.getSignature() + "。" +
            "请你严格按照这个角色设定进行对话，不要暴露你是一个 AI 模型。";

    // 获取当前Persona的API历史（如果不存在则创建）
    List<ChatRequestMessage> currentApiHistory = apiHistoryMap.computeIfAbsent(currentPersona.getName(), k -> {
        List<ChatRequestMessage> history = new ArrayList<>();
        history.add(new ChatRequestMessage("system", systemPrompt));
        return history;
    });

    // 获取当前Persona的聊天历史
    List<ChatMessage> currentUiHistory = chatHistoryLiveData.getValue();
    if (currentUiHistory == null) {
        currentUiHistory = new ArrayList<>();
    }

    // 创建用户消息并添加到UI历史
    ChatMessage uiUserMessage = new ChatMessage(userMessageText, true);
    currentUiHistory.add(uiUserMessage);
    chatHistoryLiveData.setValue(currentUiHistory);

    // 添加用户消息到API历史
    currentApiHistory.add(new ChatRequestMessage("user", userMessageText));

    ChatRequest request = new ChatRequest(BuildConfig.MODEL_NAME, currentApiHistory);

    // 异步调用API...
    // 在回调中更新当前Persona的聊天历史和API历史
}
```

## 预期效果
- 切换Persona时，聊天历史会自动切换到对应Persona的历史记录
- 重新打开聊天对话框时，可以看到上一次与该Persona的聊天历史
- 每个Persona的聊天上下文独立维护，不会相互干扰

## 注意事项
- 假设Persona名称唯一，作为Map的key
- 如果Persona名称可能重复，需要考虑添加唯一ID字段
- 考虑添加本地持久化存储，防止应用重启后聊天历史丢失（可选扩展）