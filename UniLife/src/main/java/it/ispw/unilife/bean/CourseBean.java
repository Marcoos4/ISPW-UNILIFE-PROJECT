package it.ispw.unilife.bean;

import java.util.List;

public class CourseBean{
    private String title;
    private String description;
    private int duration;
    private double fees;
    private String courseType;
    private String languageOfInstruction;
    private UniversityBean university;
    private List<String> tags;
    private List<StudentBean> interestedStudents;
    private AdmissionRequirementBean admissionRequirement;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getLanguageOfInstruction() {
        return languageOfInstruction;
    }

    public void setLanguageOfInstruction(String languageOfInstruction) { this.languageOfInstruction = languageOfInstruction; }
    public UniversityBean getUniversity() {
        return university;
    }

    public void setUniversity(UniversityBean university) {
        this.university = university;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<StudentBean> getInterestedStudents() { return interestedStudents; }
    public void setInterestedStudents(List<StudentBean> interestedStudents) { this.interestedStudents = interestedStudents; }

    public AdmissionRequirementBean getAdmissionRequirement() { return admissionRequirement; }

    public void setAdmissionRequirement(AdmissionRequirementBean admissionRequirement) { this.admissionRequirement = admissionRequirement; }
}