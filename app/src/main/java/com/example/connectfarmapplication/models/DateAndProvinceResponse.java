package com.example.connectfarmapplication.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DateAndProvinceResponse {
    private ArrayList<HashMap<String, String>> dates;
    private ArrayList<HashMap<String, String>> provinces;

    public ArrayList<String> getDates() {
        ArrayList<String> temp = new ArrayList<>();
        for (HashMap<String, String> date : dates) {
            temp.add(date.get("date"));
        }
        return temp;
    }
    public ArrayList<String> getProvinces() {
        ArrayList<String> temp = new ArrayList<>();
        for (HashMap<String, String> province : provinces) {
            temp.add(province.get("province"));
        }
        return temp;
    }
    @Override
    public String toString() {
        return "DateAndProvinceResponse{" +
                "dates=" + dates +
                ", provinces=" + provinces +
                '}';
    }
}
