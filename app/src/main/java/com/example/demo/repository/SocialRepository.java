package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.utils.MockData;
import com.example.demo.model.Post;

import java.util.List;

/**
 * 社交数据仓库类
 * 负责管理和提供社交广场的帖子数据
 * 实现Repository模式，作为数据源和UI之间的中介
 */
public class SocialRepository {

    // 社交帖子的LiveData，用于观察数据变化
    private final MutableLiveData<List<Post>> socialPostsLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     * 初始化时加载模拟社交帖子数据
     */
    public SocialRepository() {
        loadMockSocialPosts();
    }

    /**
     * 加载模拟社交帖子数据
     * 从MockData获取预设的帖子数据并更新LiveData
     */
    private void loadMockSocialPosts() {
        List<Post> mockPosts = MockData.getMockPersonaPosts();
        socialPostsLiveData.setValue(mockPosts);
    }

    /**
     * 获取社交帖子的LiveData
     * @return 可观察的社交帖子列表LiveData
     */
    public LiveData<List<Post>> getSocialPosts() {
        return socialPostsLiveData;
    }
}