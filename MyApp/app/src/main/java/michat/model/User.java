package michat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser{
    String id;
    String username;
    String fullName;
    String avatar;
    String role;
    String ngaySinh;
    String gioiTinh;
    @JsonCreator
    public User(@JsonProperty("id")String id, @JsonProperty("name")String name, @JsonProperty("fullName")String fullName, @JsonProperty("avatar")String avatar, @JsonProperty("role")String role, @JsonProperty("ngaySinh")String ngaySinh, @JsonProperty("gioiTinh")String gioiTinh) {
        this.id = id;
        this.username = name;
        this.fullName = fullName;
        this.avatar = avatar;
        this.role = role;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getName() {
        return username;
    }
    @Override
    public String getAvatar() {
        return avatar;
    }

    public String getFullName(){
        return fullName;
    }
    public String getRole() {
        return role;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
}
