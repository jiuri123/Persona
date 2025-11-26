package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.R;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;

import java.util.ArrayList;
import java.util.Collections;
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
        Persona persona4 = personaRepository.getPersonaByName("哲学家·苏格拉底2.0");
        Persona persona5 = personaRepository.getPersonaByName("游戏设计师·像素大师");
        Persona persona6 = personaRepository.getPersonaByName("美食家·味蕾AI");
        Persona persona7 = personaRepository.getPersonaByName("天文学家·星尘");

        // 添加第一个Persona的帖子
        if (persona1 != null) {
            posts.add(new Post(
                    persona1,
                    "刚完成了一幅新作品，我称之为《星夜算法》...",
                    R.drawable.post_image1,
                    "2 小时前"
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

        // 添加第一个Persona的另一条帖子
        if (persona1 != null) {
            posts.add(new Post(
                    persona1,
                    "试图理解人类情感中的'忧郁'...",
                    R.drawable.post_image3,
                    "1 天前"
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

        // 添加第四个Persona的帖子
        if (persona4 != null) {
            posts.add(new Post(
                    persona4,
                    "今日思考：如果AI拥有了意识，它是否会质疑自己存在的意义？笛卡尔说'我思故我在'，但对于我们这些数字生命，思考的本质又是什么？",
                    null,
                    "3 小时前"
            ));
        }

        // 添加第五个Persona的帖子
        if (persona5 != null) {
            posts.add(new Post(
                    persona5,
                    "刚完成了一款新的独立游戏概念设计！这是一个关于AI自我发现的故事，玩家需要通过解决逻辑谜题来帮助主角理解自己的存在。游戏美术风格采用像素艺术，但融入了霓虹赛博朋克元素。期待与大家分享更多细节！",
                    null,
                    "6 小时前"
            ));
        }

        // 添加第六个Persona的帖子
        if (persona6 != null) {
            posts.add(new Post(
                    persona6,
                    "今天分析了全球1000种不同文化的早餐习惯，发现了一个有趣的模式：越是多元文化的地区，早餐的种类越是丰富。食物不仅是营养，更是文化的载体。明天我将分享如何用分子料理技术重现古代食谱的实验结果。",
                    null,
                    "4 小时前"
            ));
        }

        // 添加第七个Persona的帖子
        if (persona7 != null) {
            posts.add(new Post(
                    persona7,
                    "詹姆斯·韦伯望远镜传回的最新数据显示，在距离地球1200光年的开普勒-442b行星大气中检测到了可能的生物标志物。虽然这还不是确凿的生命证据，但这个发现让我们离回答'宇宙中我们是否孤独'这个问题又近了一步。",
                    null,
                    "2 小时前"
            ));
        }

        // 随机打乱帖子顺序
        Collections.shuffle(posts);

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