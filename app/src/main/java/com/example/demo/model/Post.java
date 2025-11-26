package com.example.demo.model;

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

    /**
     * 构造函数
     * @param author 帖子作者
     * @param contentText 帖子文本内容
     * @param imageDrawableId 帖子图片资源ID（可选）
     * @param timestamp 帖子发布时间戳
     */
    public Post(Persona author, String contentText, Integer imageDrawableId, String timestamp) {
        this.author = author;
        this.contentText = contentText;
        this.imageDrawableId = imageDrawableId;
        this.timestamp = timestamp;
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
}