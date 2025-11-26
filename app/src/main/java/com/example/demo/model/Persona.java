package com.example.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Persona数据模型类
 * 表示应用中的虚拟角色/人格
 * 实现了Parcelable接口，以便在Intent和Bundle中传递对象
 */
public class Persona implements Parcelable {

    // Persona的名称
    private String name;
    // 头像资源ID
    private int avatarDrawableId;
    // 个人简介
    private String bio;
    // 背景故事
    private String backgroundStory;

    /**
     * 构造函数
     * @param name Persona名称
     * @param avatarDrawableId 头像资源ID
     * @param bio 个人简介
     * @param backgroundStory 背景故事
     */
    public Persona(String name, int avatarDrawableId, String bio, String backgroundStory) {
        this.name = name;
        this.avatarDrawableId = avatarDrawableId;
        this.bio = bio;
        this.backgroundStory = backgroundStory;
    }

    // Getter和Setter方法
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAvatarDrawableId() { return avatarDrawableId; }
    public void setAvatarDrawableId(int avatarDrawableId) { this.avatarDrawableId = avatarDrawableId; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getBackgroundStory() { return backgroundStory; }
    public void setBackgroundStory(String backgroundStory) { this.backgroundStory = backgroundStory; }

    /**
     * 描述内容类型，通常返回0
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将对象写入Parcel
     * 序列化对象，以便在进程间传递
     * @param dest 目标Parcel
     * @param flags 标志位
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.avatarDrawableId);
        dest.writeString(this.bio);
        dest.writeString(this.backgroundStory);
    }

    /**
     * 从Parcel中读取数据，创建Persona对象
     * @param in 源Parcel
     */
    protected Persona(Parcel in) {
        this.name = in.readString();
        this.avatarDrawableId = in.readInt();
        this.bio = in.readString();
        this.backgroundStory = in.readString();
    }

    /**
     * Parcelable接口要求的CREATOR
     * 用于从Parcel中创建Persona对象
     */
    public static final Creator<Persona> CREATOR = new Creator<Persona>() {
        @Override
        public Persona createFromParcel(Parcel source) {
            return new Persona(source);
        }

        @Override
        public Persona[] newArray(int size) {
            return new Persona[size];
        }
    };
}