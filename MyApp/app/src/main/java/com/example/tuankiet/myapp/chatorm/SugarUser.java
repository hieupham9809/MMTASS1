package com.example.tuankiet.myapp.chatorm;
import android.os.StrictMode;

import com.example.tuankiet.myapp.Constant;
import com.example.tuankiet.myapp.HttpRequest.ISugarUser;
import com.example.tuankiet.myapp.HttpRequest.ListSugarUser;
import com.example.tuankiet.myapp.HttpRequest.UserToken;
import com.example.tuankiet.myapp.UserAdapter;
import com.orm.SugarRecord;

import java.io.IOException;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import michat.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SugarUser extends SugarRecord {

        public static String TOKEN="";
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("photoURL")
        @Expose
        private String avatar;
        @SerializedName("displayName")
        @Expose
        private String displayName;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("gioiTinh")
        @Expose
        private String gioiTinh;
        @SerializedName("ngaySinh")
        @Expose
        private String ngaySinh;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;
        public SugarUser(){}

        public SugarUser(String name, String avatar, String displayName, String role, String gioiTinh, String ngaySinh) {
            this.name = name;
            this.avatar = avatar;
            this.displayName = displayName;
            this.role = role;
            this.gioiTinh = gioiTinh;
            this.ngaySinh = ngaySinh;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getGioiTinh() {
            return gioiTinh;
        }

        public void setGioiTinh(String gioiTinh) {
            this.gioiTinh = gioiTinh;
        }

        public String getNgaySinh() {
            return ngaySinh;
        }

        public void setNgaySinh(String ngaySinh) {
            this.ngaySinh = ngaySinh;
        }

    public static SugarUser getUser(String username){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<SugarUser> lst=SugarUser.find(SugarUser.class,"name=?",username);
        SugarUser sugarUser=null;
        if(lst.size()>0) sugarUser=lst.get(0);
        return sugarUser;


    }
    public static SugarUser findOwner(){
            List<SugarUser> lst=SugarUser.find(SugarUser.class,"ROLE='owner'");
            if(lst.size()==0) return null;
            return lst.get(0);
    }
    public static SugarUser loadUser(String username){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ISugarUser iSugarUser=retrofit.create(ISugarUser.class);
        Call<SugarUser> user=iSugarUser.loadUser(TOKEN,username);
        Response<SugarUser> usr = null;
        try {
            usr = user.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return usr.body();
    }
    public static SugarUser loadOwner(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ISugarUser iSugarUser=retrofit.create(ISugarUser.class);
        Call<SugarUser> user=iSugarUser.loadOwner(TOKEN);
        Response<SugarUser> usr = null;
        try {
            usr = user.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return usr.body();
    }
    public static List<SugarUser> getAllUser() throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ISugarUser iSugarUser=retrofit.create(ISugarUser.class);
            Call<ListSugarUser> lst=iSugarUser.loadAllUsers();
            Response<ListSugarUser> users=lst.execute();
            return users.body().getUsers();
    }
    public static SugarUser findByName(String name){
            List<SugarUser> user=SugarUser.find(SugarUser.class,"name=?",name);
            if(user.size()==0) return null;
            return user.get(0);
    }
    public User toUser(){
            return new User(String.valueOf(getId()),getName(),getDisplayName(),getAvatar(),getRole(),getNgaySinh(),getGioiTinh());
    }
    public static void LoginUser(String username, String password, Callback<UserToken> callback){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ISugarUser iSugarUser=retrofit.create(ISugarUser.class);
        Call<UserToken> tokenCall=iSugarUser.Signin(username,password);
        tokenCall.enqueue(callback);
    }
    public UserAdapter toUserAdapter(){
            return new UserAdapter(name,avatar,displayName,role,gioiTinh,ngaySinh,"Offline");
    }
}
