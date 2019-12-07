package com.tplink.tapo.model;

public class Subject {
    private String title;
    private String location;
    private int img;
    private int imgMore;
    private boolean isSelect;

    public Subject(String title, String location, int img, int imgMore) {
        this.title = title;
        this.img = img;
        this.imgMore = imgMore;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public int getImgMore() {
        return imgMore;
    }
}
