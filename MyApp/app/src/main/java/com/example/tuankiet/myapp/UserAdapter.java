package com.example.tuankiet.myapp;

public class UserAdapter {
    private String name;
    private String avatar;
    private String displayName;
    private String role;
    private String gioiTinh;
    private String ngaySinh;
    String status;

    public UserAdapter(String name, String avatar, String displayName, String role, String gioiTinh, String ngaySinh, String status) {
        this.name = name;
        this.avatar = avatar;
        this.displayName = displayName;
        this.role = role;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
