package com.example.disc.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MultipartBody;
import okhttp3.internal.http.HttpCodec;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    private static  Retrofit retro = null;
    private static Retrofit getClient(){
        if(retro == null)
        {
            retro  = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return  retro;}

}
