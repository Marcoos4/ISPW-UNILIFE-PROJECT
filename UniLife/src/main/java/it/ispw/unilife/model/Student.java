package it.ispw.unilife.model;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private double budget;
    private List<CourseTags> interests = new ArrayList<>();
    private List<Course> starredCourses = new ArrayList<>();

    public Student() {
        super();
        this.role = Role.STUDENT;
    }

    public Student(String username, String name, String surname, String password) {
        super(username, name, surname, password, Role.STUDENT);
    }

    public Student(String username, String name, String surname, String password, double budget, List<CourseTags> interests) {
        super(username, name, surname, password, Role.STUDENT);
        this.budget = budget;
        this.interests = interests != null ? interests : new ArrayList<>();
    }

    public List<Course> getStarredCourses() {
        return starredCourses;
    }

    public void updateStarredCourses(List<Course> starredCourses) {
        this.starredCourses = starredCourses;
    }

    public List<CourseTags>  getInterests() {
        return interests;
    }

    public void updateInterests(List<CourseTags> interests) {
        this.interests = interests;
    }

    public boolean isCourseStarred(Course course){
        return starredCourses.contains(course);
    }

    public void addInterest(CourseTags interest) {
        interests.add(interest);
    }

    public void starCourse(Course course) {
        if (!starredCourses.contains(course)) {
            starredCourses.add(course);
        }
    }

    public void unstarCourse(Course course) {
        starredCourses.remove(course);
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}