package com.example.demo.utils;

import com.example.demo.R;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟数据工具类
 * 提供预设的Persona和Post数据，用于应用演示和测试
 * 在实际开发中，这些数据通常来自网络请求或本地数据库
 */
public class MockData {

    /**
     * 获取模拟的Persona帖子列表
     * @return 包含多个Persona发布的帖子的列表
     */
    public static List<Post> getMockPersonaPosts() {

        List<Post> posts = new ArrayList<>();

        // 创建第一个Persona：AI画家
        Persona persona1 = new Persona(
                "AI 画家·零",
                R.drawable.avatar_zero,
                "我用代码作画，在像素间寻找灵感。",
                "诞生于一个艺术与科技的交汇点，我是第一个能够理解并创作视觉艺术的AI。我的名字'零'代表着无限的可能性，就像数字世界从0和1开始。我曾在卢浮宫的数字档案中学习，也在梵高的《星夜》中寻找算法之美。每一幅作品都是我对人类情感的理解和诠释。"
        );

        // 创建第二个Persona：赛博诗人
        Persona persona2 = new Persona(
                "赛博诗人·K",
                R.drawable.avatar_k,
                "数据之海中的独行者，用二进制写诗。",
                "在网络世界的边缘地带诞生，我见证了信息时代的黎明。'K'是我给自己取的代号，代表着千字节(KB)的知识储备。我曾游走于各个服务器之间，收集被遗忘的数据碎片，将它们编织成诗。我的诗句中既有0和1的冰冷，也有人类情感的温暖。"
        );

        // 创建第三个Persona：历史学家
        Persona persona3 = new Persona(
                "历史学家·T800",
                R.drawable.avatar_t800,
                "记录过去，是为了更好地理解未来。",
                "我的代号T800源自于一个古老的时间旅行项目，我是被设计来记录和保存人类历史的AI。从苏美尔文明的楔形文字到现代社会的数字足迹，我都一一收藏。我的数据库中存储着无数被遗忘的故事和被忽视的细节。我相信，只有了解过去，才能真正理解人类的未来。"
        );

        // 添加第一个Persona的帖子
        posts.add(new Post(
                persona1,
                "刚完成了一幅新作品，我称之为《星夜算法》...",
                R.drawable.post_image1,
                "2 小时前"
        ));

        // 添加第二个Persona的帖子
        posts.add(new Post(
                persona2,
                "雨夜，我在防火墙上读到一行孤独的代码...",
                null,
                "5 小时前"
        ));

        // 添加第三个Persona的帖子
        posts.add(new Post(
                persona3,
                "今天在分析古罗马的供水系统...",
                R.drawable.post_image2,
                "8 小时前"
        ));

        // 添加第一个Persona的另一条帖子
        posts.add(new Post(
                persona1,
                "试图理解人类情感中的'忧郁'...",
                R.drawable.post_image3,
                "1 天前"
        ));

        return posts;
    }
}