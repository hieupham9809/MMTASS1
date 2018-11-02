package michat.model;

public class Msg{
    String id;
    String owner;
    String createdAt;
    String text;
    String imageUrl;

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setText(String text) {
        this.text = text;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String image) {
        imageUrl=image;
    }
}