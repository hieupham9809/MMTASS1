package com.example.tuankiet.myapp.HttpRequest;

import com.example.tuankiet.myapp.chatorm.SugarUser;

import michat.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ISugarUser {
    @GET("/users")
    Call<ListSugarUser> loadUsers(@Header("Access-Token") String token);

    @GET("/users/online")
    Call<ListSugarUser> loadOnlineUsers(@Header("Access-Token") String token);

    @GET("/users/me")
    Call<SugarUser> loadUser(@Header("Access-Token") String token);

    @POST("/users/signin")
    @FormUrlEncoded
    Call<UserToken> Signin(@Field("email") String email,
                           @Field("password") String password);
    //{
    // idToken:'sgkjalkjljgda;'
    //}
    //
}
