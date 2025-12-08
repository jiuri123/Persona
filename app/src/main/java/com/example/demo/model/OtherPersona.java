package com.example.demo.model;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * OtherPersona数据模型类
 * 表示系统预设的虚拟角色/人格
 * 继承自Persona抽象类，添加@Entity注解用于本地存储
 */
@Entity(tableName = "other_personas")
public class OtherPersona extends Persona {

    @PrimaryKey
    private long id;

    @NonNull
    private String name;
    private int avatarDrawableId;
    private String avatarUri;
    private String signature;
    private String backgroundStory;
    private String gender;
    private int age;
    private String personality;
    private String relationship;
    
    // 创建时间戳
    @NonNull
    private long createdAt;

    /**
     * 构造函数
     * @param id OtherPersona唯一标识符（0表示由系统自动生成）
     * @param name OtherPersona名称
     * @param avatarDrawableId 头像资源ID
     * @param avatarUri 头像URI（用于从相册选择的图片）
     * @param signature 个性签名
     * @param backgroundStory 背景故事
     * @param gender 性别
     * @param age 年龄
     * @param personality 性格
     * @param relationship 关系（和我的关系）
     * @param createdAt 创建时间戳
     */
    public OtherPersona(long id, @NonNull String name, int avatarDrawableId, String avatarUri, String signature, String backgroundStory,
                       String gender, int age, String personality, String relationship, @NonNull long createdAt) {
        super(id, name, avatarDrawableId, avatarUri, signature, backgroundStory,
                gender, age, personality, relationship, createdAt);
        this.id = id;
        this.name = name;
        this.avatarDrawableId = avatarDrawableId;
        this.avatarUri = avatarUri;
        this.signature = signature;
        this.backgroundStory = backgroundStory;
        this.gender = gender;
        this.age = age;
        this.personality = personality;
        this.relationship = relationship;
        this.createdAt = createdAt;
    }

    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public void setId(long id) {
        super.setId(id);
        this.id = id;
    }
    
    @Override
    @NonNull
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(@NonNull String name) {
        super.setName(name);
        this.name = name;
    }
    
    @Override
    public int getAvatarDrawableId() {
        return avatarDrawableId;
    }
    
    @Override
    public void setAvatarDrawableId(int avatarDrawableId) {
        super.setAvatarDrawableId(avatarDrawableId);
        this.avatarDrawableId = avatarDrawableId;
    }
    
    @Override
    public String getAvatarUri() {
        return avatarUri;
    }
    
    @Override
    public void setAvatarUri(String avatarUri) {
        super.setAvatarUri(avatarUri);
        this.avatarUri = avatarUri;
    }
    
    @Override
    public String getSignature() {
        return signature;
    }
    
    @Override
    public void setSignature(String signature) {
        super.setSignature(signature);
        this.signature = signature;
    }
    
    @Override
    public String getBackgroundStory() {
        return backgroundStory;
    }
    
    @Override
    public void setBackgroundStory(String backgroundStory) {
        super.setBackgroundStory(backgroundStory);
        this.backgroundStory = backgroundStory;
    }
    
    @Override
    public String getGender() {
        return gender;
    }
    
    @Override
    public void setGender(String gender) {
        super.setGender(gender);
        this.gender = gender;
    }
    
    @Override
    public int getAge() {
        return age;
    }
    
    @Override
    public void setAge(int age) {
        super.setAge(age);
        this.age = age;
    }
    
    @Override
    public String getPersonality() {
        return personality;
    }
    
    @Override
    public void setPersonality(String personality) {
        super.setPersonality(personality);
        this.personality = personality;
    }
    
    @Override
    public String getRelationship() {
        return relationship;
    }
    
    @Override
    public void setRelationship(String relationship) {
        super.setRelationship(relationship);
        this.relationship = relationship;
    }
    
    @Override
    @NonNull
    public long getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public void setCreatedAt(@NonNull long createdAt) {
        super.setCreatedAt(createdAt);
        this.createdAt = createdAt;
    }

    /**
     * 从Parcel中读取数据，创建OtherPersona对象
     * @param in 源Parcel
     */
    protected OtherPersona(Parcel in) {
        super(in);
        this.id = in.readLong();
        this.name = Objects.requireNonNull(in.readString());
        this.avatarDrawableId = in.readInt();
        this.avatarUri = in.readString();
        this.signature = in.readString();
        this.backgroundStory = in.readString();
        this.gender = in.readString();
        this.age = in.readInt();
        this.personality = in.readString();
        this.relationship = in.readString();
        this.createdAt = in.readLong();
    }

    /**
     * Parcelable接口要求的CREATOR
     * 用于从Parcel中创建OtherPersona对象
     */
    public static final Creator<OtherPersona> CREATOR = new Creator<OtherPersona>() {
        @Override
        public OtherPersona createFromParcel(Parcel source) {
            return new OtherPersona(source);
        }

        @Override
        public OtherPersona[] newArray(int size) {
            return new OtherPersona[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.avatarDrawableId);
        dest.writeString(this.avatarUri);
        dest.writeString(this.signature);
        dest.writeString(this.backgroundStory);
        dest.writeString(this.gender);
        dest.writeInt(this.age);
        dest.writeString(this.personality);
        dest.writeString(this.relationship);
        dest.writeLong(this.createdAt);
    }
}