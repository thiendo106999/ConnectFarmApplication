package com.example.connectfarmapplication.models;


import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo {
    private String id;
    private String name;
    private String job;
    private String address;
    private String year_of_birth;
    private String phone_number;
    private String avatar = "default";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return "Nghề nghiệp: " + job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAddress() {
        return "Địa chỉ: " + address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getYear_of_birth() {
        return "Năm sinh: " + year_of_birth;
    }

    public void setYear_of_birth(String year_of_birth) {
        this.year_of_birth = year_of_birth;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAvatar() {
        return "http://192.168.1.7:8000/api/storage/" + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", job='" + job + '\'' +
                ", address='" + address + '\'' +
                ", year_of_birth='" + year_of_birth + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @BindingAdapter("setThumbnail")
    public static void loadAvatar(CircleImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.dandelion)
                .into(view);
    }
}