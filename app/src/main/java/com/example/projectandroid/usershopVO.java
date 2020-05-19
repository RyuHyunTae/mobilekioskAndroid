package com.example.projectandroid;

public class usershopVO {

    int usershopNum;
    String id;

    String businessNum;
    String password;
    String name;
    String shopName;
    String shopAddress;
    String shopTime;
    String QRcode;
    int approval;
    public int getUsershopNum() {
        return usershopNum;
    }
    public void setUsershopNum(int usershopNum) {
        this.usershopNum = usershopNum;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getBusinessNum() {
        return businessNum;
    }
    public void setBusinessNum(String businessNum) {
        this.businessNum = businessNum;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getShopAddress() {
        return shopAddress;
    }
    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }
    public String getShopTime() {
        return shopTime;
    }
    public void setShopTime(String shopTime) {
        this.shopTime = shopTime;
    }
    public String getQRcode() {
        return QRcode;
    }
    public void setQRcode(String qRcode) {
        QRcode = qRcode;
    }
    public int getApproval() {
        return approval;
    }
    public void setApproval(int approval) {
        this.approval = approval;
    }
    @Override
    public String toString() {
        return "usershopVO [usershopNum=" + usershopNum + ", id=" + id + ", businessNum=" + businessNum + ", password="
                + password + ", name=" + name + ", shopName=" + shopName + ", shopAddress=" + shopAddress
                + ", shopTime=" + shopTime + ", QRcode=" + QRcode + ", approval=" + approval + "]";
    }


}
