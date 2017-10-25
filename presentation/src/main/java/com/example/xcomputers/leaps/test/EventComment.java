package com.example.xcomputers.leaps.test;

/**
 * Created by Ivan on 9/21/2017.
 */

public class EventComment {

    private String userName;
    private String userComment;
    private String date;
    private float rating;

    public EventComment(String userName, String userComment, String date, float rating) {
        this.userName = userName;
        this.userComment = userComment;
        this.date = date;
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
