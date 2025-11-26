package com.example.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Persona implements Parcelable {

    private String name;
    private int avatarDrawableId;
    private String bio;
    private String backgroundStory;

    public Persona(String name, int avatarDrawableId, String bio, String backgroundStory) {
        this.name = name;
        this.avatarDrawableId = avatarDrawableId;
        this.bio = bio;
        this.backgroundStory = backgroundStory;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAvatarDrawableId() { return avatarDrawableId; }
    public void setAvatarDrawableId(int avatarDrawableId) { this.avatarDrawableId = avatarDrawableId; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getBackgroundStory() { return backgroundStory; }
    public void setBackgroundStory(String backgroundStory) { this.backgroundStory = backgroundStory; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.avatarDrawableId);
        dest.writeString(this.bio);
        dest.writeString(this.backgroundStory);
    }

    protected Persona(Parcel in) {
        this.name = in.readString();
        this.avatarDrawableId = in.readInt();
        this.bio = in.readString();
        this.backgroundStory = in.readString();
    }

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