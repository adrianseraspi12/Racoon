package com.suzei.racoon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Groups implements Parcelable {

    private HashMap<String, Object> admin, members;
    private String description, image, name, uid;

    public Groups() {
    }


    protected Groups(Parcel in) {
        description = in.readString();
        image = in.readString();
        name = in.readString();
        uid = in.readString();
        admin = (HashMap<String, Object>) in.readSerializable();
        members = (HashMap<String, Object>) in.readSerializable();
    }

    public static final Creator<Groups> CREATOR = new Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public HashMap<String, Object> getAdmin() {
        return admin;
    }

    public void setAdmin(HashMap<String, Object> admin) {
        this.admin = admin;
    }

    public HashMap<String, Object> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Object> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeSerializable(admin);
        dest.writeSerializable(members);
    }
}
