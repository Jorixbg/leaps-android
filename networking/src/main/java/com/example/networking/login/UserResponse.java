package com.example.networking.login;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.feed.trainer.Image;
import com.example.networking.following.user.Followed;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xComputers on 03/06/2017.
 */

public class UserResponse implements Entity {

    private static final long serialVersionUID = 5859832874313588599L;

    @SerializedName("user_id")
    private int userId;
    @SerializedName("username")
    private String username;
    @SerializedName("email_address")
    private String email;
    @SerializedName("age")
    private int age;
    @SerializedName("gender")
    private String gender;
    @SerializedName("attended_events")
    private int attendedEvents;
    @SerializedName("location")
    private String address;
    @SerializedName("max_distance_setting")
    private int maxDistance;
    @SerializedName("profile_image_url")
    private String profileImage;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("birthday")
    private long birthDay;
    @SerializedName("firebase_token")
    private String firebase_token;
    @SerializedName("description")
    private String description;
    @SerializedName("free_event")
    private boolean hasFreeEvent;
    @SerializedName("is_trainer")
    private boolean isTrainer;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("years_of_training")
    private int yearsOfTraining;
    @SerializedName("session_price")
    private int sesionPrice;
    @SerializedName("long_description")
    private String longDescription;
    @SerializedName("specialties")
    private List<String> tags;
    @SerializedName("images")
    private List<Image> images;
    @SerializedName("attending_events")
    private List<RealEvent> attendingEvents;
    @SerializedName("hosting_events")
    private List<RealEvent> hostingEvents;
    @SerializedName("followed_by")
    private Followed followers;
    @SerializedName("rating")
    private float rating;
    @SerializedName("reviews")
    private int reviews;

    @Override
    public float rating() {
        return rating;
    }

    @Override
    public int reviews() {
        return reviews;
    }

    @Override
    public int userId() {
        return userId;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public int age() {
        return age;
    }

    @Override
    public String gender() {
        return gender;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public int maxDistance() {
        return maxDistance;
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public long birthDay() {
        return birthDay;
    }

    @Override
    public String firebaseToken() {
        return firebase_token;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean hasFreeEvent() {
        return hasFreeEvent;
    }

    @Override
    public boolean isTrainer() {
        return isTrainer;
    }

    @Override
    public String phoneNumber() {
        return phoneNumber;
    }

    @Override
    public int yearsOfTraining() {
        return yearsOfTraining;
    }

    @Override
    public int sesionPrice() {
        return sesionPrice;
    }

    @Override
    public String longDescription() {
        return longDescription;
    }

    @Override
    public String profileImageUrl() {
        return profileImage;
    }

    @Override
    public List<Image> images() {
        return images;
    }

    @Override
    public List<String> specialities() {
       return tags;

    }

    @Override
    public List<Event> attending() {
        List<Event> list = new ArrayList<>();
        list.addAll(attendingEvents);
        return list;
    }

    @Override
    public List<RealEvent> hosting() {
        List<RealEvent> list = new ArrayList<>();
        list.addAll(hostingEvents);
        return list;
    }


    public Followed followers() {
        Followed followersObj= followers;
        return followersObj;
    }

    @Override
    public Followed filter() {
        return null;
    }

    public void setFollowers(Followed followers) {
        this.followers = followers;
    }

    @Override
    public int attendedEvents() {
        return attendedEvents;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHasFreeEvent(boolean hasFreeEvent) {
        this.hasFreeEvent = hasFreeEvent;
    }

    public void setTrainer(boolean trainer) {
        isTrainer = trainer;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setYearsOfTraining(int yearsOfTraining) {
        this.yearsOfTraining = yearsOfTraining;
    }

    public void setSesionPrice(int sesionPrice) {
        this.sesionPrice = sesionPrice;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setAttendingEvents(List<RealEvent> attendingEvents) {
        this.attendingEvents = attendingEvents;
    }

    public void setHostingEvents(List<RealEvent> hostingEvents) {
        this.hostingEvents = hostingEvents;
    }

}
