package com.example.projectandroid;

public class User {
    String id;
    String password;
    String name;
    String phone;


    public User(String id, String password, String name,String phone) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phone=phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswoard() {
        return password;
    }

    public void setPasswoard(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserVO [id=" + id + ", password=" + password + ", name=" + name + ", phone=" + phone + "]";
    }
}
