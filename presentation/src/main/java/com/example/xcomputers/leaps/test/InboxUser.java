package com.example.xcomputers.leaps.test;

/**
 * Created by IvanGachmov on 12/1/2017.
 */

public class InboxUser {

    private String name;
    private String image;
    private String id;

    public InboxUser() {
    }

    public InboxUser(String name, String image, String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}