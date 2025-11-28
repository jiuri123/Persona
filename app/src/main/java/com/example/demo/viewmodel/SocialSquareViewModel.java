package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.repository.OtherPersonaPostRepository;
import com.example.demo.repository.UserFollowedListRepository;
import com.example.demo.repository.UserPersonaPostRepository;
import com.example.demo.repository.UserPersonaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 社交广场ViewModel
 * 负责管理社交广场的所有数据和业务逻辑
 * 聚合所需的数据源，处理帖子合并、关注/取消关注、生成新帖子等逻辑
 */
public class SocialSquareViewModel extends ViewModel {

    // 关注列表仓库
    private final UserFollowedListRepository userFollowedListRepository;
    
    // 用户Persona仓库
    private final UserPersonaRepository userPersonaRepository;
    
    // 其他Persona帖子仓库
    private final OtherPersonaPostRepository otherPersonaPostRepository;
    
    // 用户Persona帖子仓库
    private final UserPersonaPostRepository userPersonaPostRepository;
    
    // 合并后的帖子列表LiveData
    private final MutableLiveData<List<Post>> mergedPostsLiveData = new MutableLiveData<>(new ArrayList<>());
    
    // 加载状态LiveData
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    
    // 错误信息LiveData
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     * 初始化所有仓库实例
     */
    public SocialSquareViewModel() {
        this.userFollowedListRepository = UserFollowedListRepository.getInstance();
        this.userPersonaRepository = UserPersonaRepository.getInstance();
        this.otherPersonaPostRepository = OtherPersonaPostRepository.getInstance();
        this.userPersonaPostRepository = UserPersonaPostRepository.getInstance();
        
        // 初始化合并帖子列表
        mergePosts();
        
        // 设置仓库的观察器
        setupRepositoryObservers();
    }

    /**
     * 设置仓库的观察器
     * 当各个仓库的数据发生变化时，更新合并后的帖子列表
     */
    private void setupRepositoryObservers() {
        // 观察用户Persona帖子变化
        userPersonaPostRepository.getMyPostsLiveData().observeForever(posts -> {
            mergePosts();
        });
        
        // 观察其他Persona帖子变化
        otherPersonaPostRepository.getSocialPosts().observeForever(posts -> {
            mergePosts();
        });
        
        // 观察用户Persona帖子加载状态
        userPersonaPostRepository.getIsLoading().observeForever(isLoading -> {
            isLoadingLiveData.setValue(isLoading);
        });
        
        // 观察用户Persona帖子错误信息
        userPersonaPostRepository.getError().observeForever(error -> {
            errorLiveData.setValue(error);
        });
    }

    /**
     * 合并帖子列表
     * 将用户的帖子和其他Persona的帖子合并，用户的帖子显示在前面
     */
    private void mergePosts() {
        List<Post> mergedPosts = new ArrayList<>();
        
        // 获取用户的帖子
        List<Post> myPosts = userPersonaPostRepository.getMyPostsLiveData().getValue();
        if (myPosts != null) {
            mergedPosts.addAll(myPosts);
        }
        
        // 获取其他Persona的帖子
        List<Post> otherPosts = otherPersonaPostRepository.getSocialPosts().getValue();
        if (otherPosts != null) {
            mergedPosts.addAll(otherPosts);
        }
        
        // 更新合并后的帖子列表
        mergedPostsLiveData.setValue(mergedPosts);
    }

    /**
     * 获取合并后的帖子列表LiveData
     * @return 合并后的帖子列表LiveData
     */
    public LiveData<List<Post>> getMergedPostsLiveData() {
        return mergedPostsLiveData;
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态LiveData
     */
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息LiveData
     */
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    /**
     * 生成新帖子
     * 通过UserPersonaPostRepository生成新帖子
     */
    public void generateNewPost() {
        if (!userPersonaRepository.hasCurrentUserPersona()) {
            // 如果当前用户没有选择用户Persona，则无法生成新帖子
            errorLiveData.setValue("请先去创建你的Persona~");
            return;
        }else{
            userPersonaPostRepository.generateNewPost(userPersonaRepository.getCurrentUserPersona().getValue());
        }
    }

    /**
     * 处理关注/取消关注操作
     * @param persona 要关注/取消关注的Persona
     */
    public void onFollowClick(Persona persona) {
        if (isFollowingPersona(persona)) {
            // 如果已关注，则取消关注
            userFollowedListRepository.removeFollowedPersona(persona);
        } else {
            // 如果未关注，则添加关注
            userFollowedListRepository.addFollowedPersona(persona);
        }
    }

    /**
     * 检查是否已关注指定Persona
     * @param persona 要检查的Persona
     * @return 如果已关注返回true，否则返回false
     */
    public boolean isFollowingPersona(Persona persona) {
        return userFollowedListRepository.isFollowingPersona(persona);
    }

    /**
     * 获取已关注Persona列表LiveData
     * @return 已关注Persona列表LiveData
     */
    public LiveData<List<Persona>> getFollowedPersonasLiveData() {
        return userFollowedListRepository.getFollowedPersonas();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        userPersonaPostRepository.clearError();
    }

    /**
     * 检查是否有当前用户Persona
     * @return 如果有当前用户Persona返回true，否则返回false
     */
    public boolean hasCurrentUserPersona() {
        return userPersonaRepository.hasCurrentUserPersona();
    }
}