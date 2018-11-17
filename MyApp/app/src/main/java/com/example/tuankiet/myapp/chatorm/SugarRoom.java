package com.example.tuankiet.myapp.chatorm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SugarRoom extends SugarRecord {
    String name;
    String members;//id and not contains owner (owner default must have)
    String owner;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
    public static SugarRoom findByName(String name){
        Iterator<SugarRoom> lst = SugarRoom.findAll(SugarRoom.class);
        while(lst.hasNext()){
            SugarRoom room=lst.next();
            ArrayList<String> members=new Gson().fromJson(room.members,new TypeToken<ArrayList<String>>(){}.getType());
                if(members.contains(name)) {
                    if (members.size() == 1) return room;
                }
            }
        return null;
    }
    public static SugarRoom findGroupByName(String groupName){
        Iterator<SugarRoom> lst=SugarRoom.findAll(SugarRoom.class);
        while(lst.hasNext()){
            SugarRoom room=lst.next();
            if(groupName.equals(room.getName()+":"+room.getOwner()+":"+room.getMembers()))
                return room;
        }
        return null;
    }
    public void addMember(String name){
        ArrayList<String> members=new Gson().fromJson(getMembers(),new TypeToken<ArrayList<String>>(){}.getType());
        if(!members.contains(name)) members.add(name);
        setMembers(new Gson().toJson(members));
        this.save();
    }
    public  void removeMember(String name){
        ArrayList<String> members=new Gson().fromJson(getMembers(),new TypeToken<ArrayList<String>>(){}.getType());
        if(members.contains(name)) members.remove(name);
        setMembers(new Gson().toJson(members));
        this.save();
    }
    public String[] deleteAllMessage(){

        List<SugarMessage> msg=SugarMessage.find(SugarMessage.class,"ROOM_ID=?",String.valueOf(getId()));
        String[] ids=new String[msg.size()];
        int j=0;
        for(SugarMessage i:msg){
            i.delete();
            ids[j++]= String.valueOf(i.getId());
        }
        return ids;

    }
}
