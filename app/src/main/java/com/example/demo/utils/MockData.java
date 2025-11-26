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
                "..."
        );

        // 创建第二个Persona：赛博诗人
        Persona persona2 = new Persona(
                "赛博诗人·K",
                R.drawable.avatar_k,
                "数据之海中的独行者，用二进制写诗。",
                "..."
        );

        // 创建第三个Persona：历史学家
        Persona persona3 = new Persona(
                "历史学家·T800",
                R.drawable.avatar_t800,
                "记录过去，是为了更好地理解未来。",
                "..."
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