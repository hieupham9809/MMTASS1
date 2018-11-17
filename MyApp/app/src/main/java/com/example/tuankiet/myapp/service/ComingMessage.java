package com.example.tuankiet.myapp.service;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComingMessage {

    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("group")
    @Expose
    private String group;///Name:Owner:members
    @SerializedName("header")
    @Expose
    private String header;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("owner")
    @Expose
    private String owner;

    //{header:"msg",msg:"abc",owner:"hieu",group:"name:member1,member2,member3",createdAt:"1540909632977"}
    //{header:"createGroup",msg:"[a,b,c,d]","owner:",group}
    //room: {name: ,members:member1,member2,member3, isGroup:}
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String toJson(){
        Gson gson=new Gson();
        return gson.toJson(this);
    }
    public static ComingMessage fromString(String str){
        return new Gson().fromJson(str,ComingMessage.class);

    }
}