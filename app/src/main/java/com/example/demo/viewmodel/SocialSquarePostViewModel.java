package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Post;
import com.example.demo.repository.SocialRepository;

import java.util.List;

/**
 * 社交广场ViewModel类
 * 管理社交广场相关的数据和操作
 * 负责获取和展示社交动态列表
 * 遵循MVVM架构模式，负责UI与数据之间的交互
 */
public class SocialSquarePostViewModel extends ViewModel {

    // 社交数据仓库和社交动态LiveData
    private final SocialRepository socialRepository;
    private final LiveData<List<Post>> socialPostsLiveData;

    /**
     * 构造函数
     * 初始化社交数据仓库和LiveData
     */
    public SocialSquarePostViewModel() {
        socialRepository = new SocialRepository();
        socialPostsLiveData = socialRepository.getSocialPosts();
    }

    /**
     * 获取社交动态LiveData
     * @return 社交动态列表的LiveData对象，UI组件可以观察此数据变化
     */
    public LiveData<List<Post>> getSocialPosts() {
        return socialPostsLiveData;
    }
}