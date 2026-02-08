package it.ispw.unilife.model;

import it.ispw.unilife.enums.Role;
import it.ispw.unilife.model.notification.Notification;

import java.util.ArrayList;
import java.util.List;

public abstract class User{
    protected String username;
    protected String name;
    protected String surname;
    protected String password;
    protected Role role;
    protected List<Notification> notifications = new ArrayList<>();

    protected User() {
    }

    protected User(String username, String name, String surname, String password, Role role) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.role = role;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
