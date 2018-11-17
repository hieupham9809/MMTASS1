package com.example.tuankiet.myapp.HttpRequest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {
    @POST("/files")
    @Multipart
    Call<FileUpload> uploadFile(@Header("Access-Token") String token,
                                @Part MultipartBody.Part file);
}
