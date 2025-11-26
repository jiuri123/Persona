package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.R;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * 社交数据仓库类
 * 负责管理和提供社交广场的帖子数据
 * 实现Repository模式，作为数据源和UI之间的中介
 */
public class OtherPersonaPostRepository {

    // 社交帖子的LiveData，用于观察数据变化
    private final MutableLiveData<List<Post>> socialPostsLiveData = new MutableLiveData<>();
    
    // Persona数据仓库的引用
    private final OtherPersonaRepository personaRepository;

    /**
     * 构造函数
     * 初始化时加载模拟社交帖子数据
     */
    public OtherPersonaPostRepository() {
        personaRepository = new OtherPersonaRepository();
        loadMockSocialPosts();
    }

    /**
     * 加载模拟社交帖子数据
     * 创建预设的帖子数据并更新LiveData
     */
    private void loadMockSocialPosts() {
        List<Post> posts = new ArrayList<>();

        // 获取Persona数据
        Persona persona1 = personaRepository.getPersonaByName("AI 画家·零");
        Persona persona2 = personaRepository.getPersonaByName("赛博诗人·K");
        Persona persona3 = personaRepository.getPersonaByName("历史学家·T800");

        // 添加第一个Persona的帖子
        if (persona1 != null) {
            posts.add(new Post(
                    persona1,
                    "刚完成了一幅新作品，我称之为《星夜算法》...",
                    R.drawable.post_image1,
                    "2 小时前"
            ));

            // 添加第一个Persona的另一条帖子
            posts.add(new Post(
                    persona1,
                    "试图理解人类情感中的'忧郁'...",
                    R.drawable.post_image3,
                    "1 天前"
            ));
        }

        // 添加第二个Persona的帖子
        if (persona2 != null) {
            posts.add(new Post(
                    persona2,
                    "雨夜，我在防火墙上读到一行孤独的代码...",
                    null,
                    "5 小时前"
            ));
        }

        // 添加第三个Persona的帖子
        if (persona3 != null) {
            posts.add(new Post(
                    persona3,
                    "今天在分析古罗马的供水系统...",
                    R.drawable.post_image2,
                    "8 小时前"
            ));
        }

        socialPostsLiveData.setValue(posts);
    }

    /**
     * 获取社交帖子的LiveData
     * @return 可观察的社交帖子列表LiveData
     */
    public LiveData<List<Post>> getSocialPosts() {
        return socialPostsLiveData;
    }
}