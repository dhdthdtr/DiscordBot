package org.example.utils;

public class StringUtils {
    public static String TOKEN = "YOUR_TOKEN";
    public static String IMDB_API_KEY = "API_KEY";
    public static String DB_URL = "DB_URL";
    public static String USER_NAME = "USER_NAME";
    public static String PASSWORD = "PASSWORD";
    public static String EMAIL_SERVER = "EMAIL_SERVER";
    public static String PWD_SERVER = "PWD_SERVER";
    public static String SERPER_API_KEY = "SERPER_API_KEY";

    public String delStrNull(String s) {
        if(s != null || s.isEmpty()){
            return "";
        }
        return s;
    }
}
