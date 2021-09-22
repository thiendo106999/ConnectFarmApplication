package com.example.connectfarmapplication.models;


import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class User {
    private String id;
    private String year_born;
    private String gender;
    private String email;
    private String address;
    private String phone_number;
    private String full_name;
    private String status = "online";
    private String avatar = "default";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear_born() {
        return year_born;
    }

    public void setYear_born(String year_born) {
        this.year_born = year_born;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public User() {
    }

    public User(String id, String nick_name, String avatar) {
        this.id = id;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", year_born='" + year_born + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", full_name='" + full_name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @BindingAdapter("setThumbnail")
    public static void loadAvatar(CircleImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.dandelion)
                .into(view);
    }
}