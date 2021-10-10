package com.example.connectfarmapplication.retrofit;

public class APIUtils {
    public static final String BASE_URL = "http://192.168.1.7:8000/";

    public static DataClient getDataClient() {
        return RetrofitClient.getClient(BASE_URL).create(DataClient.class);
    }
}

