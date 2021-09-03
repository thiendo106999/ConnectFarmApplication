package com.example.connectfarmapplication.models;

import android.media.Image;

import java.util.ArrayList;

public class New {
    private String id;
    private String content;
    private String likes;
    private String user_id;
    private ArrayList<Image> images;
    private String tag;
    private String time;


    public New() {
    }

    public New(
            String id, String content, String likes, String user_id, ArrayList<Image> images, String tag, String time
    ) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.user_id = user_id;
        this.images = images;
        this.tag = tag;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}