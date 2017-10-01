package com.example.xcomputers.leaps;


import com.example.xcomputers.leaps.utils.SectionedDataHolder;

import java.util.List;


/**
 * Created by xComputers on 03/06/2017.
 */

public class User {

    private static User instance;

    private long userId;
    private String username;
    private String email;
    private int age;
    private String gender;
    private String address;
    private int maxDistance;
    private String firstName;
    private String lastName;
    private long birthDay;
    private String description;
    private boolean hasFreeEvent;
    private boolean isTrainer;
    private String phoneNumber;
    private int yearsOfTraining;
    private double sesionPrice;
    private String longDescription;
    private SectionedDataHolder attending;
    private SectionedDataHolder hosting;
    private double longtitude = 0;
    private double lattitude = 0;
    private List<String> specs;
    private String imageUrl;



    private User(){}

    public static synchronized User getInstance(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }



    public static void clear(){
        instance = null;
    }

    public void setAttending(SectionedDataHolder attending) {
        this.attending = attending;
    }

    public void setHosting(SectionedDataHolder hosting) {
        this.hosting = hosting;
    }

    public SectionedDataHolder getAttending() {

        return attending;
    }

    public SectionedDataHolder getHosting() {
        return hosting;
    }
    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHasFreeEvent() {
        return hasFreeEvent;
    }

    public boolean isTrainer() {
        return isTrainer;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getYearsOfTraining() {
        return yearsOfTraining;
    }

    public double getSesionPrice() {
        return sesionPrice;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setUserId(long userId) {
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

    public void setSesionPrice(double sesionPrice) {
        this.sesionPrice = sesionPrice;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
    }
}
