package com.example.networking.feed.trainer;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.following.user.Followed;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xComputers on 15/06/2017.
 */

public interface Entity extends Serializable {

    int userId();
    String username();
    String email();
    int age();
    String gender();
    String address();
    int maxDistance();
    String firstName();
    String lastName();
    long birthDay();
    String firebaseToken();
    String description();
    boolean hasFreeEvent();
    boolean isTrainer();
    String phoneNumber();
    int yearsOfTraining();
    int sesionPrice();
    String longDescription();
    String profileImageUrl();
    List<Image> images();
    List<String> specialities();
    List<Event> attending();
    List<RealEvent> hosting();
    Followed followers();
    Followed filter();
    int attendedEvents();
    float rating();
    int reviews();

}
