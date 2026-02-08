package it.ispw.unilife.model;

import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.CourseType;
import it.ispw.unilife.model.admission.AbstractRequirement;
import it.ispw.unilife.model.admission.AdmissionRequirements;

import java.util.ArrayList;
import java.util.List;

public class Course{

    // --- Fields ---
    private String title;
    private String description;
    private int duration;
    private double fees;
    private CourseType courseType;
    private String languageOfInstruction;
    private University university;
    private List<CourseTags> tags;
    private AdmissionRequirements admissionRequirements;

    // --- Constructors ---
    public Course() {
        this.tags = new ArrayList<>();
        this.admissionRequirements = new AdmissionRequirements();
    }

    public Course(String title, String description, CourseType courseType,
                  String languageOfInstruction, University university, List<CourseTags> tags, AdmissionRequirements requirements) {
        this.title = title;
        this.description = description;
        this.university = university;
        this.courseType = courseType;
        this.languageOfInstruction = languageOfInstruction;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        this.admissionRequirements = requirements;
    }

    public void setTags(List<CourseTags> tags) {
        this.tags = tags;

    }

    public void addTag(CourseTags tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    public void removeTag(CourseTags tag) {
        tags.remove(tag);

    }

    // --- Other Logic ---

    public void addRequirement(AbstractRequirement requirement) {
        if (this.admissionRequirements == null) this.admissionRequirements = new AdmissionRequirements();
        admissionRequirements.addRequirement(requirement);
    }

    // --- Getters & Setters (One-liners) ---
    public String getCourseTitle() { return title; }
    public void setCourseTitle(String title) { this.title = title; }

    public String getCourseDescription() { return description; }
    public void setCourseDescription(String description) { this.description = description; }

    public int getCourseDuration() { return duration; }
    public void setCourseDuration(int duration) { this.duration = duration; }

    public double getCourseFees() { return fees; }
    public void setCourseFees(double fees) { this.fees = fees; }

    public CourseType getCourseType() { return courseType; }
    public void setCourseType(CourseType courseType) { this.courseType = courseType; }

    public String getLanguageOfInstruction() { return languageOfInstruction; }
    public void setLanguageOfInstruction(String languageOfInstruction) { this.languageOfInstruction = languageOfInstruction; }

    public University getUniversity() { return university; }
    public void setUniversity(University university) { this.university = university; }

    public AdmissionRequirements getAdmissionRequirements() { return admissionRequirements; }
    public void setAdmissionRequirements(AdmissionRequirements admissionRequirements) { this.admissionRequirements = admissionRequirements; }

    public List<CourseTags> getTags() { return tags; }

    // Helper per ottenere la lista dei requirements evitando null
    public List<AbstractRequirement> getRequirements() { return (admissionRequirements != null) ? admissionRequirements.getRequirements() : new ArrayList<>(); }

    // --- Tag Management (Logic + Notifications) ---


}