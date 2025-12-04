package com.example.demo.model;

import java.util.Objects;

/**
 * Post UI模型类
 * 用于包装Post原始数据和UI状态（是否已关注）
 * 实现了equals和hashCode方法，确保DiffUtil能正确比较
 */
public class PostUiItem {
    // 原始帖子数据
    private final Post post;
    // 是否已关注帖子作者
    private final boolean isFollowed;

    /**
     * 构造函数
     * @param post 原始帖子数据
     * @param isFollowed 是否已关注帖子作者
     */
    public PostUiItem(Post post, boolean isFollowed) {
        this.post = post;
        this.isFollowed = isFollowed;
    }

    /**
     * 获取原始帖子数据
     * @return 原始帖子数据
     */
    public Post getPost() {
        return post;
    }

    /**
     * 检查是否已关注帖子作者
     * @return 是否已关注
     */
    public boolean isFollowed() {
        return isFollowed;
    }

    /**
     * 重写equals方法，比较两个PostUiItem是否相等
     * @param o 要比较的对象
     * @return 如果相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostUiItem that = (PostUiItem) o;
        return isFollowed == that.isFollowed &&
                Objects.equals(post, that.post);
    }

    /**
     * 重写hashCode方法，生成对象的哈希值
     * @return 对象的哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(post, isFollowed);
    }
}
