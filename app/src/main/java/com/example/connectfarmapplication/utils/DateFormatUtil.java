package com.example.connectfarmapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
    public static String formatDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateFormat = new Date();
        try {
            dateFormat = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(dateFormat);
    }

    public static String formatToServer(String date){
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date dateFormat = new Date();
        try {
            dateFormat = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(dateFormat);
    }
}
