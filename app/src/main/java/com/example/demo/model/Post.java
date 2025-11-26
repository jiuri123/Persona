package com.example.demo.model;

public class Post {

    private Persona author;

    private String contentText;
    private Integer imageDrawableId;

    private String timestamp;

    public Post(Persona author, String contentText, Integer imageDrawableId, String timestamp) {
        this.author = author;
        this.contentText = contentText;
        this.imageDrawableId = imageDrawableId;
        this.timestamp = timestamp;
    }

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