package it.ispw.unilife.bean;

import java.time.LocalDateTime;

public class LessonBean {
    private TutorBean tutor;
    private String subject;
    private int durationInHours;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private float price;

    public TutorBean getTutor() {return tutor;}
    public void setTutor(TutorBean tutor) {this.tutor = tutor;}
    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    public int getDurationInHours() {return durationInHours;}
    public void setDurationInHours(int durationInHours) {this.durationInHours = durationInHours;}
    public LocalDateTime getStartTime() {return startTime;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
    public LocalDateTime getEndTime() {return endTime;}
    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}
    public float getPrice() {return price;}
    public void setPrice(float price) {this.price = price;}
}
