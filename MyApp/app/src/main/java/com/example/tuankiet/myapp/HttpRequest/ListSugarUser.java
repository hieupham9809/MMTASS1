package com.example.tuankiet.myapp.HttpRequest;

import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListSugarUser {
    @SerializedName("users")
    @Expose
    List<SugarUser> users;

    public ListSugarUser(List<SugarUser> users) {
        this.users = users;
    }

    public List<SugarUser> getUsers() {
        return users;
    }

    public void setUsers(List<SugarUser> users) {
        this.users = users;
    }
}
