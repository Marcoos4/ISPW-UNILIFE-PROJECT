package it.ispw.unilife.bean;

import java.time.LocalDateTime;

public class FilterTutorBean {
    private LocalDateTime start;
    private LocalDateTime end;
    private String subject;
    private float amount;

    public LocalDateTime getStart() {return start;}
    public void setStart(LocalDateTime start) {this.start = start;}
    public LocalDateTime getEnd() {return end;}
    public void setEnd(LocalDateTime end) {this.end = end;}
    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    public float getAmount() {return amount;}
    public void setAmount(float amount) {this.amount = amount;}
}
