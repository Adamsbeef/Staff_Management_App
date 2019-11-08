package com.example.disc.api;

import com.example.disc.models.ApiResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Apiservice {
    @Multipart
    @POST("image/upload")
    Call<ApiResponse> uploadImage (@Part MultipartBody.Part image,@Part ("upload_preset")RequestBody upload_preset);
}
