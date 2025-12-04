package com.example.demo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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
    
    // 合并后的帖子列表LiveData
    private final MediatorLiveData<List<Post>> mergedPostsLiveData = new MediatorLiveData<>();
    
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
        return followedPersonasLiveData;
    }

    /**
     * 设置MediatorLiveData观察Repository的LiveData
     */
    private void setupMediatorLiveData() {
        // 观察已关注Persona列表变化
        followedPersonasLiveData.addSource(userFollowedListRepository.getFollowedPersonas(), followedPersonasLiveData::setValue);
        
        // 观察用户Persona列表变化
        userPersonasLiveData.addSource(userPersonaRepository.getUserPersonas(), userPersonasLiveData::setValue);
        
        // 观察用户Persona帖子变化，用于合并帖子
        mergedPostsLiveData.addSource(userPersonaPostRepository.getMyPostsLiveData(), posts -> {
            mergePosts();
        });
        
        // 观察其他Persona帖子变化，用于合并帖子
        mergedPostsLiveData.addSource(otherPersonaPostRepository.getSocialPosts(), posts -> {
            mergePosts();
        });
    }
    
    /**
     * 获取用户Persona列表LiveData
     * @return 用户Persona列表LiveData
     */
    public LiveData<List<Persona>> getUserPersonasLiveData() {
        return userPersonasLiveData;
    }


    /**
     * 6. 暴露这个新的、加工后的 "状态" LiveData 给 View
     * @return 一个 LiveData，如果用户有 Persona 则为 true
     */
    public LiveData<Boolean> getHasUserPersonaState() {
        return hasUserPersona;
    }
}