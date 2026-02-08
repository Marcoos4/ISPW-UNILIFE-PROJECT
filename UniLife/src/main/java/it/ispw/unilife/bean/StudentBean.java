package it.ispw.unilife.bean;

import java.util.ArrayList;
import java.util.List;

public class StudentBean {
    private String name;
    private String surname;
    private String username;
    private double budget;
    private List<String> interests = new ArrayList<>();
    private List<CourseBean> starredCourses = new ArrayList<>();
    private List<ApplicationBean> applications = new ArrayList<>();

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public double getBudget() {return budget;}
    public void setBudget(double budget) {this.budget = budget;}

    public List<ApplicationBean> getApplications() {return applications;}
    public void setApplications(List<ApplicationBean> applications) {this.applications = applications;}

    public List<CourseBean> getStarredCourses() { return starredCourses; }
    public void setStarredCourses(List<CourseBean> starredCourses) { this.starredCourses = starredCourses; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
}
