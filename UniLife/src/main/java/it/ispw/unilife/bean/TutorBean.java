package it.ispw.unilife.bean;

public class TutorBean {
    private String name;
    private String surname;
    private String username;
    private float rating;

    // --- GETTERS & SETTERS ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public float getRating() {return rating;}
    public void setRating(float rating){this.rating = rating;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
}