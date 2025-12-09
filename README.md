# 安卓虚拟角色社交应用

一个基于Java开发的Android应用，允许用户创建、管理和与虚拟角色进行互动交流的社交平台。

## 功能特性

- **虚拟角色创建与管理**：用户可自定义创建具有独特背景故事、个性特征和外观的虚拟角色
- **智能对话系统**：通过AI技术实现与虚拟角色的自然语言交流
- **社交广场**：浏览和发布虚拟角色相关的帖子内容
- **关注机制**：关注感兴趣的其他虚拟角色
- **Markdown支持**：支持富文本内容显示，提升阅读体验
- **数据持久化**：本地存储用户创建的虚拟角色和聊天历史

## 技术栈

- **语言**：Java 11
- **框架**：Android SDK (API 24-36)
- **数据库**：Room (本地数据存储)
- **网络请求**：Retrofit 2 + OkHttp
- **图片加载**：Glide 4
- **架构组件**：ViewModel + LiveData
- **UI组件**：Material Design
- **富文本渲染**：Markwon
- **视图绑定**：ViewBinding

## 快速开始

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 11 或更高版本
- Android SDK 36 (API 36) 及兼容库
- Gradle 8.0+ (使用Kotlin DSL)

### 构建步骤

1. 克隆项目仓库
   ```bash
   git clone <项目URL>
   cd <项目目录>
   ```

2. 配置API密钥
   - 在项目根目录创建或编辑 `gradle.properties` 文件
   - 添加以下配置（请替换为实际的API密钥）
     ```properties
     org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
     android.useAndroidX=true
     android.nonTransitiveRClass=true
     API_KEY=your_api_key_here
     MODEL_NAME=your_model_name_here
     ```

3. 使用Android Studio打开项目

4. 构建并运行项目
   - 连接Android设备或启动模拟器
   - 点击 "Run" 按钮或使用快捷键 `Shift + F10`

## 项目结构

```
app/src/main/
├── java/com/example/demo/
│   ├── activity/      # 活动类，处理UI交互和导航
│   ├── adapter/       # 适配器类，用于RecyclerView数据展示
│   ├── fragment/      # 碎片类，实现UI模块化
│   ├── model/         # 数据模型类，定义核心数据结构
│   ├── network/       # 网络相关类，处理API通信
│   ├── repository/    # 仓库类，封装数据访问逻辑
│   ├── utils/         # 工具类，提供通用功能
│   └── viewmodel/     # 视图模型类，连接UI和数据层
└── res/               # 资源文件（布局、图片、样式等）
```

### 核心模块说明

- **model**：定义Persona(虚拟角色)、ChatMessage(聊天消息)、Post(帖子)等核心数据结构
- **repository**：实现数据访问逻辑，包括本地数据库操作和网络请求
- **viewmodel**：提供UI所需的数据，管理UI状态
- **activity/fragment**：实现用户界面和交互逻辑
- **utils**：包含文本渲染、打字机效果等实用工具类

## 注意事项

- **API密钥**：应用需要配置有效的AI服务API密钥才能使用聊天功能
- **权限**：应用需要网络权限进行API通信，以及存储权限用于选择头像图片
- **兼容性**：最低支持Android 7.0 (API 24)，推荐在Android 10.0+上使用
- **性能优化**：对于大量聊天消息或图片，可能需要进一步优化加载和渲染性能
- **离线功能**：当前版本主要支持在线模式，离线功能有限

## License

[MIT](https://choosealicense.com/licenses/mit/)
