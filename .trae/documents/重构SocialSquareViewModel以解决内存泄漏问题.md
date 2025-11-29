# 重构SocialSquareViewModel以解决内存泄漏问题

## 问题分析

当前代码中使用了多个`observeForever`方法，这会导致内存泄漏，因为这些观察者没有被正确移除。

## 重构方案

1. **移除所有observeForever调用**
2. **使用MediatorLiveData替代observeForever**：

   * 为合并帖子创建MediatorLiveData，观察用户帖子和其他Persona帖子

   * 为加载状态创建MediatorLiveData，观察仓库的加载状态

   * 为错误信息创建MediatorLiveData，观察仓库的错误信息
3. **优化setupMediatorLiveData方法**，将所有观察逻辑集中管理
4. **移除setupRepositoryObservers方法**，因为所有观察逻辑都将通过MediatorLiveData实现

## 重构步骤

1. 修改mergedPostsLiveData为MediatorLiveData，观察userPersonaPostRepository.getMyPostsLiveData()和otherPersonaPostRepository.getSocialPosts()
2. 修改isLoadingLiveData为MediatorLiveData，观察userPersonaPostRepository.getIsLoading()
3. 修改errorLiveData为MediatorLiveData，观察userPersonaPostRepository.getError()
4. 在setupMediatorLiveData方法中添加所有观察逻辑
5. 移除setupRepositoryObservers方法
6. 确保mergePosts方法在任何依赖的LiveData变化时都能被调用

## 预期效果

* 解决内存泄漏问题

* 代码结构更清晰，所有观察逻辑集中管理

* 符合LiveData最佳实践，使用MediatorLiveData处理多个数据源的依赖关系

