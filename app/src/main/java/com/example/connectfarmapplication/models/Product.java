package com.example.connectfarmapplication.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Product {
    private String image;
    private String name;
    private String userName;
    private String address;
    private String phoneNumber;
    private String date;
    private String hexta;

    public Product() {
    }

    public Product(String image, String name, String userName, String address, String phoneNumber, String date, String hexta) {
        this.image = image;
        this.name = name;
        this.userName = userName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.hexta = hexta;
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

    public String getUserName() {
        return "Tên: " +userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return "Địa chỉ: " + address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return "Số điện thoại: " + phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return "Ngày thu hoạch: " + date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHexta() {
        return "Số lượng: " + hexta +" hecta";
    }

    public void setHexta(String hexta) {
        this.hexta = hexta;
    }

    @BindingAdapter("setThumbnail")
    public static void loadAvatar(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.dandelion)
                .into(view);
    }
}
