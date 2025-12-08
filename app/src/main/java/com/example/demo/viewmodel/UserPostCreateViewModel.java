package com.example.demo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.UserPersona;
import com.example.demo.model.Post;
import com.example.demo.data.repository.UserPersonaPostRepository;

/**
 * 发布动态编辑页面的ViewModel
 * 负责管理编辑页面的业务逻辑
 * 包括AI扩展、AI生成和发布动态功能
 */
public class UserPostCreateViewModel extends AndroidViewModel {

    // 用户Persona帖子仓库，用于处理AI生成、扩展和发布帖子
    private final UserPersonaPostRepository userPersonaPostRepository;

    // LiveData对象，用于观察数据变化
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> generatedContentLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPublishedLiveData = new MutableLiveData<>(false);

    /**
     * 构造函数
     * 初始化所有仓库实例
     * @param application Application实例
     */
    public UserPostCreateViewModel(Application application) {
        super(application);
        this.userPersonaPostRepository = UserPersonaPostRepository.getInstance();
    }

    /**
     * AI扩展当前内容
     * 根据指定的Persona设定扩展已有内容
     * @param persona 指定的Persona
     * @param currentContent 当前编辑框中的内容
     */
    public void aiExpandContent(UserPersona persona, String currentContent) {
        // 检查Persona是否为空
        if (persona == null) {
            errorLiveData.setValue("请先选择一个Persona~");
            return;
        }

        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 调用仓库的AI扩展方法
        userPersonaPostRepository.aiExpandContent(
                persona,
                currentContent,
                new UserPersonaPostRepository.ContentCallback() {
                    @Override
                    public void onSuccess(String content) {
                        // 扩展成功，更新内容
                        generatedContentLiveData.postValue(content);
                        isLoadingLiveData.postValue(false);
                    }

                    @Override
                    public void onError(String error) {
                        // 扩展失败，更新错误信息
                        errorLiveData.postValue(error);
                        isLoadingLiveData.postValue(false);
                    }
                }
        );
    }

    /**
     * AI生成新内容
     * 根据指定的Persona设定生成新内容
     * @param persona 指定的Persona
     */
    public void aiGenerateContent(UserPersona persona) {
        // 检查Persona是否为空
        if (persona == null) {
            errorLiveData.setValue("请先选择一个Persona~");
            return;
        }

        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 调用仓库的AI生成方法
        userPersonaPostRepository.aiGenerateContent(
                persona,
                new UserPersonaPostRepository.ContentCallback() {
                    @Override
                    public void onSuccess(String content) {
                        // 生成成功，更新内容
                        generatedContentLiveData.postValue(content);
                        isLoadingLiveData.postValue(false);
                    }

                    @Override
                    public void onError(String error) {
                        // 生成失败，更新错误信息
                        errorLiveData.postValue(error);
                        isLoadingLiveData.postValue(false);
                    }
                }
        );
    }

    /**
     * 发布动态
     * 将编辑框中的内容发布为新帖子
     * @param content 要发布的动态内容
     * @param persona 发布动态的Persona
     */
    public void publishPost(UserPersona persona, String content) {
        // 检查Persona是否为空
        if (persona == null) {
            errorLiveData.setValue("请先选择一个Persona~");
            return;
        }

        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 调用仓库的发布帖子方法
        userPersonaPostRepository.publishPost(
                persona,
                content,
                new UserPersonaPostRepository.PublishCallback() {
                    @Override
                    public void onSuccess(Post post) {
                        // 发布成功，更新发布状态
                        isPublishedLiveData.postValue(true);
                        isLoadingLiveData.postValue(false);
                    }

                    @Override
                    public void onError(String error) {
                        // 发布失败，更新错误信息
                        errorLiveData.postValue(error);
                        isLoadingLiveData.postValue(false);
                    }
                }
        );
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * 获取AI生成/扩展的内容LiveData
     * @return 生成内容的LiveData对象
     */
    public LiveData<String> getGeneratedContent() {
        return generatedContentLiveData;
    }

    /**
     * 获取发布状态LiveData
     * @return 发布状态的LiveData对象
     */
    public LiveData<Boolean> getIsPublished() {
        return isPublishedLiveData;
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }
}