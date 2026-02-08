package it.ispw.unilife.model;

import it.ispw.unilife.enums.Role;
import it.ispw.unilife.model.notification.Notification;

import java.util.ArrayList;
import java.util.List;

public class Tutor extends User{
    private float rating;

    public Tutor() {
        super();
        this.role = Role.TUTOR;
    }

    public Tutor(String username, String name, String surname, String password) {
        super(username, name, surname, password, Role.TUTOR);
    }

    public Tutor(String username, String name, String surname, String password, List<Notification> notifications, float rating) {
        super(username, name, surname, password, Role.TUTOR);
        this.notifications = notifications != null ? notifications : new ArrayList<>();
        this.rating = rating;
    }

    public float getRating() {
        return this.rating;
    }

    public void updateRating(float rating) {
        this.rating = rating;
    }

}
