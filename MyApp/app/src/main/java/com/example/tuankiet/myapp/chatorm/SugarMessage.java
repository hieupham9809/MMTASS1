package com.example.tuankiet.myapp.chatorm;

import com.example.tuankiet.myapp.service.ComingMessage;
import com.orm.SugarRecord;

import java.util.Date;
import java.util.List;

import michat.model.Message;
import michat.model.User;
public class SugarMessage extends SugarRecord {
    String text;
    long roomId;
    long owner;
    Date createdAt;
    Date receivedAt;
    Date readAt;
    String imageUrl;
    String status;
    public SugarMessage(){}

    public SugarMessage(String text, long owner, long roomId, Date createdAt, Date receivedAt, Date readAt, String imageUrl) {
        this.text = text;
        this.owner = owner;
        this.createdAt = createdAt;
        this.receivedAt = receivedAt;
        this.readAt = readAt;
        this.imageUrl = imageUrl;
        this.roomId=roomId;
    }
    public Message toMessage(){

        Message msg=new Message(String.valueOf(this.getId()),this.text,SugarUser.findById(SugarUser.class,this.owner).toUser(),this.createdAt,this.receivedAt,this.readAt);
        msg.setImageUrl(this.imageUrl);
        return msg;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
