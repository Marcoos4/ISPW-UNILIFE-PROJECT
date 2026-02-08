package it.ispw.unilife.bean;

import java.util.ArrayList;
import java.util.List;

public class UniversityBean{
    private String name;
    private String location;
    private int ranking;
    private double livingCosts;
    private String contactInfo;

    private List<CourseBean> courses = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public double getLivingCosts() {
        return livingCosts;
    }

    public void setLivingCosts(double livingCosts) {
        this.livingCosts = livingCosts;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<CourseBean> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseBean> courses) {
        this.courses = courses;
    }
}