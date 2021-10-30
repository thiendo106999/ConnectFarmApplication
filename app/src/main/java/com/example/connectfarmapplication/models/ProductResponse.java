package com.example.connectfarmapplication.models;

import com.example.connectfarmapplication.retrofit.APIUtils;

public class ProductResponse {
    private int id;
    private String image;
    private String name;
    private String user_name;
    private String address;
    private String phone_number;
    private String date;
    private String hexta;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return APIUtils.PATH + image;
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

    public String getUser_name() {
        return "Họ và Tên: " + user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAddress() {
        return "Địa chỉ: " + address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return "Số điện thoại: " + phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDate() {
        return "Ngày thu hoạch: "+ date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHexta() {
        return "Số lượng: " + hexta + " mẫu";
    }

    public void setHexta(String hexta) {
        this.hexta = hexta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
