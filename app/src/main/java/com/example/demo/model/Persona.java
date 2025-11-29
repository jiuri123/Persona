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
    // 头像URI（用于从相册选择的图片）
    private String avatarUri;
    // 背景故事
    private String backgroundStory;
    // 性别
    private String gender;
    // 年龄
    private int age;
    // 性格
    private String personality;
    // 关系（和我的关系）
    private String relationship;
    // 个性签名
    private String signature;

    /**
     * 构造函数
     * @param name Persona名称
     * @param avatarDrawableId 头像资源ID
     * @param avatarUri 头像URI（用于从相册选择的图片）
     * @param signature 个性签名
     * @param backgroundStory 背景故事
     * @param gender 性别
     * @param age 年龄
     * @param personality 性格
     * @param relationship 关系（和我的关系）
     */
    public Persona(String name, int avatarDrawableId, String avatarUri, String signature, String backgroundStory, 
                   String gender, int age, String personality, String relationship) {
        this.name = name;
        this.avatarDrawableId = avatarDrawableId;
        this.avatarUri = avatarUri;
        this.backgroundStory = backgroundStory;
        this.gender = gender;
        this.age = age;
        this.personality = personality;
        this.relationship = relationship;
        this.signature = signature;
    }

    // Getter和Setter方法
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAvatarDrawableId() { return avatarDrawableId; }
    public void setAvatarDrawableId(int avatarDrawableId) { this.avatarDrawableId = avatarDrawableId; }
    public String getAvatarUri() { return avatarUri; }
    public void setAvatarUri(String avatarUri) { this.avatarUri = avatarUri; }
    public String getBackgroundStory() { return backgroundStory; }
    public void setBackgroundStory(String backgroundStory) { this.backgroundStory = backgroundStory; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getPersonality() { return personality; }
    public void setPersonality(String personality) { this.personality = personality; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

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
        dest.writeString(this.avatarUri);
        dest.writeString(this.backgroundStory);
        dest.writeString(this.gender);
        dest.writeInt(this.age);
        dest.writeString(this.personality);
        dest.writeString(this.relationship);
        dest.writeString(this.signature);
    }

    /**
     * 从Parcel中读取数据，创建Persona对象
     * @param in 源Parcel
     */
    protected Persona(Parcel in) {
        this.name = in.readString();
        this.avatarDrawableId = in.readInt();
        this.avatarUri = in.readString();
        this.backgroundStory = in.readString();
        this.gender = in.readString();
        this.age = in.readInt();
        this.personality = in.readString();
        this.relationship = in.readString();
        this.signature = in.readString();
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