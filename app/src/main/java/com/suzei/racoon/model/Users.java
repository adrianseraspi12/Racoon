package com.suzei.racoon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {

    private int age;
    private Boolean online;
    private String bio, gender, image, name, device_token, uid;

    public Users() {
    }

    protected Users(Parcel in) {
        age = in.readInt();
        online = (Boolean) in.readValue(Boolean.class.getClassLoader());
        bio = in.readString();
        gender = in.readString();
        image = in.readString();
        name = in.readString();
        device_token = in.readString();
        uid = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(age);
//        dest.writeByte((byte) (online ? 1 : 0));
        dest.writeValue(online);
        dest.writeString(bio);
        dest.writeString(gender);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(device_token);
        dest.writeString(uid);
    }
}
