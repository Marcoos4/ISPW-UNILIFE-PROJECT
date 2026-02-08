package it.ispw.unilife.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationBean {
    private CourseBean courseBean;
    private StudentBean studentBean;
    private String status;
    private String submissionDate;
    private LocalDateTime creationDate;
    private List<ApplicationItemBean> items;

    public ApplicationBean() {
        this.items = new ArrayList<>();
    }

    public CourseBean getCourseBean() { return courseBean; }
    public void setCourseBean(CourseBean courseBean) { this.courseBean = courseBean; }

    public StudentBean getStudentName() { return studentBean; }
    public void setStudentName(StudentBean studentName) { this.studentBean = studentName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(String submissionDate) { this.submissionDate = submissionDate; }

    public List<ApplicationItemBean> getItems() { return items; }
    public void setItems(List<ApplicationItemBean> items) { this.items = items; }

    public LocalDateTime getCreationDate() {return creationDate;}

    public void setCreationDate(LocalDateTime creationDate) {this.creationDate = creationDate;}
}

