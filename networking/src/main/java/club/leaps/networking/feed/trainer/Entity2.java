package club.leaps.networking.feed.trainer;

import club.leaps.networking.feed.event.Event;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xComputers on 15/06/2017.
 */

public interface Entity2 extends Serializable {

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
    List<Event> hosting();
    int attendedEvents();
}
