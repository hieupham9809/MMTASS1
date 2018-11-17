package com.example.tuankiet.myapp.HttpRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserToken {
    @SerializedName("idToken")
    @Expose
    private String token;

    public UserToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
