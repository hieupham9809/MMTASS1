package michat.model;

import android.support.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;

public class Message implements IMessage, MessageContentType.Image{
    String id;
    String text;
    User owner;
    Date createdAt;
    Date receivedAt;
    Date readAt;
    String imageUrl;

    public Message(String id, String text, User owner, Date createdAt, Date receivedAt,Date readAt) {
        this.id = id;
        this.text = text;
        this.owner = owner;
        this.createdAt = createdAt;
        this.receivedAt = receivedAt;
        this.readAt=readAt;
        imageUrl=null;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getText() {
        return text;
    }
    @Override
    public User getUser() {
        return owner;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public Date getReceivedAt() {
        return receivedAt;
    }
    public Date getReadAt(){return readAt;}
    public void setImageUrl(String image){
        imageUrl=image;
    }
    @Nullable
    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }
}
