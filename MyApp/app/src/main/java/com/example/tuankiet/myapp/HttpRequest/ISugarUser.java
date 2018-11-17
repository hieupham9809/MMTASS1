package com.example.tuankiet.myapp.HttpRequest;

import com.example.tuankiet.myapp.chatorm.SugarUser;

import michat.model.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ISugarUser {
    @GET("users")
    Call<ListSugarUser> loadUsers(@Header("Access-Token") String token);

    @GET("users/all")
    Call<ListSugarUser> loadAllUsers();

    @GET("users/me")
    Call<SugarUser> loadOwner(@Header("Access-Token") String token);

    @GET("users")
    Call<SugarUser> loadUser(@Header("Access-Token") String token, @Query("name") String username);

    @POST("users/signin")
    @FormUrlEncoded
    Call<UserToken> Signin(@Field("email") String email,
                           @Field("password") String password);
}
