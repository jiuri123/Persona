package com.example.demo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.model.PostUiItem;
import com.example.demo.data.repository.OtherPersonaPostRepository;
import com.example.demo.data.repository.UserFollowedListRepository;
import com.example.demo.data.repository.UserPersonaPostRepository;
import com.example.demo.data.repository.UserPersonaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 社交广场ViewModel
 * 负责管理社交广场的所有数据和业务逻辑
 * 聚合所需的数据源，处理帖子合并、关注/取消关注、生成新帖子等逻辑
 */
public class SocialSquareViewModel extends AndroidViewModel {

    // 关注列表仓库
    private final UserFollowedListRepository userFollowedListRepository;
    
    // 用户Persona仓库
    private final UserPersonaRepository userPersonaRepository;
    
    // 其他Persona帖子仓库
    private final OtherPersonaPostRepository otherPersonaPostRepository;
    
    // 用户Persona帖子仓库
    private final UserPersonaPostRepository userPersonaPostRepository;

    // 检查用户是否创建了Persona
    private final LiveData<Boolean> hasUserPersona;

    // 用户Persona帖子列表
    List<Post> userPersonaPosts;
    // 其他Persona帖子列表
    List<Post> otherPersonaPosts;
    // 已关注的Persona列表
    List<Persona> followedPersonas;
    // 合并后的帖子UI列表LiveData
    private final MediatorLiveData<List<PostUiItem>> mergedPostsLiveData = new MediatorLiveData<>();
    
    // 已关注Persona列表LiveData，使用MediatorLiveData包装Repository的LiveData
    private final MediatorLiveData<List<Persona>> followedPersonasLiveData = new MediatorLiveData<>();
    
    // 用户Persona列表LiveData，使用MediatorLiveData包装Repository的LiveData
    private final MediatorLiveData<List<Persona>> userPersonasLiveData = new MediatorLiveData<>();

    /**
     * 构造函数
     * 初始化所有仓库实例
     * @param application Application实例
     */
    public SocialSquareViewModel(Application application) {
        super(application);
        this.userFollowedListRepository = UserFollowedListRepository.getInstance();
        this.userPersonaRepository = UserPersonaRepository.getInstance(application);
        this.otherPersonaPostRepository = OtherPersonaPostRepository.getInstance();
        this.userPersonaPostRepository = UserPersonaPostRepository.getInstance();

        // 3. “加工” userPersonasLiveData
        // Transformations.map 会自动观察 userPersonasLiveData
        // 每当 List<Persona> 变化时，它会自动执行 -> 后的代码
        hasUserPersona = Transformations.map(userPersonasLiveData, personas -> {
            // 这就是“加工”逻辑
            return personas != null && !personas.isEmpty();
        });

        // 设置MediatorLiveData观察Repository的LiveData
        setupMediatorLiveData();
        
        // 初始化合并帖子列表
        mergePosts();
    }

    /**
     * 设置MediatorLiveData观察Repository的LiveData
     */
    private void setupMediatorLiveData() {
        // 观察已关注Persona列表变化
        followedPersonasLiveData.addSource(userFollowedListRepository.getFollowedPersonas(), personas -> {
            followedPersonasLiveData.setValue(personas);
            followedPersonas = personas;
            mergePosts();
        });

        // 观察用户Persona列表变化
        userPersonasLiveData.addSource(userPersonaRepository.getUserPersonas(), userPersonasLiveData::setValue);

        // 观察用户Persona帖子变化，用于合并帖子
        mergedPostsLiveData.addSource(userPersonaPostRepository.getUserPostsLiveData(), posts -> {
            userPersonaPosts = posts;
            mergePosts();
        });

        // 观察其他Persona帖子变化，用于合并帖子
        mergedPostsLiveData.addSource(otherPersonaPostRepository.getSocialPosts(), posts -> {
            otherPersonaPosts = posts;
            mergePosts();
        });
        
        // 观察已关注Persona列表变化，确保关注状态变化时能更新UI
        mergedPostsLiveData.addSource(followedPersonasLiveData, personas -> {
            mergePosts();
        });
    }

    /**
     * 合并帖子列表并生成PostUiItem
     * 将用户的帖子和其他Persona的帖子合并，用户的帖子显示在前面
     * 同时检查每个帖子的作者是否已被关注
     */
    private void mergePosts() {
        List<PostUiItem> mergedPostUiItems = new ArrayList<>();

        // 先添加用户的帖子（用户的帖子显示在前面）
        if (userPersonaPosts != null) {
            for (Post post : userPersonaPosts) {
                // 用户自己的帖子，不需要关注
                PostUiItem postUiItem = new PostUiItem(post, false);
                mergedPostUiItems.add(postUiItem);
            }
        }

        // 再添加其他Persona的帖子
        if (otherPersonaPosts != null) {
            for (Post post : otherPersonaPosts) {
                // 检查作者是否已被关注
                boolean isFollowed = isFollowedPersona(post.getAuthor());
                PostUiItem postUiItem = new PostUiItem(post, isFollowed);
                mergedPostUiItems.add(postUiItem);
            }
        }

        // 更新合并后的帖子UI列表
        mergedPostsLiveData.setValue(mergedPostUiItems);
    }

    /**
     * 检查指定Persona是否已被关注
     * @param persona 要检查的Persona
     * @return 是否已关注
     */
    public boolean isFollowedPersona(Persona persona) {
        if (followedPersonas == null || persona == null) {
            return false;
        }
        for (Persona followedPersona : followedPersonas) {
            // 先比较id字段，如果id不为0且相等，则返回true
            if (followedPersona.getId() != 0 && persona.getId() != 0 && followedPersona.getId() == persona.getId()) {
                return true;
            }
            // 如果id为0或不相等，则比较name字段
            if (followedPersona.getName() != null && persona.getName() != null && 
                followedPersona.getName().equals(persona.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取合并后的帖子UI列表LiveData
     * @return 合并后的帖子UI列表LiveData
     */
    public LiveData<List<PostUiItem>> getMergedPostsLiveData() {
        return mergedPostsLiveData;
    }

    /**
     * 处理关注/取消关注操作
     * @param persona 要关注/取消关注的Persona
     */
    public void onFollowClick(Persona persona) {
        if (isFollowedPersona(persona)) {
            // 如果已关注，则取消关注
            userFollowedListRepository.removeFollowedPersona(persona);
        } else {
            // 如果未关注，则添加关注
            userFollowedListRepository.addFollowedPersona(persona);
        }
    }



    /**
     * 6. 暴露这个新的、加工后的 "状态" LiveData 给 View
     * @return 一个 LiveData，如果用户有 Persona 则为 true
     */
    public LiveData<Boolean> getHasUserPersonaState() {
        return hasUserPersona;
    }
}