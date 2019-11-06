package com.example.disc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.internal.http.HttpCodec;

public class ApiUtil {
    ApiUtil(){}
    public static final  String BASE_URL = "https://api.cloudinary.com/v1_1/mike12";

    public static URL buildUrl(String title){
        String fullUrl = BASE_URL+"/image/upload";
        URL url = null;
        try {
            url = new URL(fullUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static  void getJson(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();


    }
}
