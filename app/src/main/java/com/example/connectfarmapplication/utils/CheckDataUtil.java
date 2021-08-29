package com.example.connectfarmapplication.utils;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckDataUtil {
    public static boolean checkEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean checkPhone(String phone) {
        Pattern regexPattern = Pattern.compile("^[+]?[0-9]{10,13}$");
        Matcher regMatcher = regexPattern.matcher(phone);

        return regMatcher.matches();
    }
}
