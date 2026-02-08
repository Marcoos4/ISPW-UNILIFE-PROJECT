package it.ispw.unilife.bean;

public class FilterCourseBean {

    private String universityLocation;
    private String universityRankingRange;
    private String universityName;
    private String courseType;
    private String languageOfInstruction;
    private String courseDurationRange;

    public String getUniversityLocation() {
        return universityLocation;
    }

    public void setUniversityLocation(String universityLocation) {
        this.universityLocation = universityLocation;
    }

    public String getUniversityRankingRange() {
        return universityRankingRange;
    }

    public void setUniversityRankingRange(String universityRankingRange) {
        this.universityRankingRange = universityRankingRange;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
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

    public void setLanguageOfInstruction(String languageOfInstruction) {
        this.languageOfInstruction = languageOfInstruction;
    }

    public String getCourseDurationRange() {
        return courseDurationRange;
    }

    public void setCourseDurationRange(String courseDurationRange) {
        this.courseDurationRange = courseDurationRange;
    }
}