package com.example.demo.model;

import java.util.Objects;

/**
 * 帖子数据模型类
 * 表示Persona在社交广场发布的帖子
 */
public class Post {

    // 帖子作者
    private Persona author;
    // 帖子文本内容
    private String contentText;
    // 帖子图片资源ID（可选）
    private Integer imageDrawableId;
    // 帖子发布时间戳
    private String timestamp;
    // 是否是用户创建的persona帖子
    private boolean isUserPersonaPost;

    /**
     * 构造函数
     * @param author 帖子作者
     * @param contentText 帖子文本内容
     * @param imageDrawableId 帖子图片资源ID（可选）
     * @param timestamp 帖子发布时间戳
     */
    public Post(Persona author, String contentText, Integer imageDrawableId, String timestamp, boolean isUserPersonaPost) {
        this.author = author;
        this.contentText = contentText;
        this.imageDrawableId = imageDrawableId;
        this.timestamp = timestamp;
        this.isUserPersonaPost = isUserPersonaPost;
    }

    // Getter和Setter方法
    public Persona getAuthor() {
        return author;
    }

    public void setAuthor(Persona author) {
        this.author = author;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Integer getImageDrawableId() {
        return imageDrawableId;
    }

    public void setImageDrawableId(Integer imageDrawableId) {
        this.imageDrawableId = imageDrawableId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUserPersonaPost() {
        return isUserPersonaPost;
    }

    public void setUserPersonaPost(boolean userPersonaPost) {
        isUserPersonaPost = userPersonaPost;
    }

    /**
     * 重写equals方法，比较两个Post对象是否相等
     * @param o 要比较的对象
     * @return 如果相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return isUserPersonaPost == post.isUserPersonaPost &&
                Objects.equals(author, post.author) &&
                Objects.equals(contentText, post.contentText) &&
                Objects.equals(imageDrawableId, post.imageDrawableId) &&
                Objects.equals(timestamp, post.timestamp);
    }

    /**
     * 重写hashCode方法，生成对象的哈希值
     * @return 对象的哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(author, contentText, imageDrawableId, timestamp, isUserPersonaPost);
    }
}