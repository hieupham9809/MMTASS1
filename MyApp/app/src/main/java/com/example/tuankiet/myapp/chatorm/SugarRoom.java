package com.example.tuankiet.myapp.chatorm;

import com.orm.SugarRecord;

import java.util.List;

public class SugarRoom extends SugarRecord {
    String name;
    String members;//id and not contains owner (owner default must have)
    boolean isGroup;
    public void setName(String name) {
        this.name = name;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }
    public SugarRoom(){}
    public SugarRoom(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
    public static SugarRoom findByName(String name){
        List<SugarRoom> user=SugarUser.find(SugarRoom.class,"name=?",name);
        if(user.size()==0) return null;
        return user.get(0);
    }
}
