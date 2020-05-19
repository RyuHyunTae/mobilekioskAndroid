package com.example.projectandroid;

public class menuVO {

    int menuNum;
    String menuName;
    String menuDescription;
    int menuPrice;
    String menuPicture;
    String businessNum;

    public menuVO(int menuNum , String menuName, String menuDescription , int menuPrice , String menuPicture , String businessNum){
        this.menuNum = menuNum;
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPrice=menuPrice;
        this.menuPicture=menuPicture;
        this.businessNum=businessNum;
    }
    public int getMenuNum() {
        return menuNum;
    }
    public void setMenuNum(int menuNum) {
        this.menuNum = menuNum;
    }
    public String getMenuName() {
        return menuName;
    }
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    public String getMenuDescription() {
        return menuDescription;
    }
    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }
    public int getMenuPrice() {
        return menuPrice;
    }
    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }
    public String getMenuPicture() {
        return menuPicture;
    }
    public void setMenuPicture(String menuPicture) {
        this.menuPicture = menuPicture;
    }
    public String getBusinessNum() {
        return businessNum;
    }
    public void setBusinessNum(String businessNum) {
        this.businessNum = businessNum;
    }
    @Override
    public String toString() {
        return "menuVO [menuNum=" + menuNum + ", menuName=" + menuName + ", menuDescription=" + menuDescription
                + ", menuPrice=" + menuPrice + ", menuPicture=" + menuPicture + ", businessNum=" + businessNum + "]";
    }


}
