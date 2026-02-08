package it.ispw.unilife.bean;

public class ReservationBean {
    private PaymentBean payment;
    private LessonBean lesson;
    private StudentBean student;
    private String status;

    public PaymentBean getPayment() {return payment;}
    public void setPayment(PaymentBean payment) {this.payment = payment;}
    public LessonBean getLesson() {return lesson;}
    public void setLesson(LessonBean lesson) {this.lesson = lesson;}
    public StudentBean getStudent() {return student;}
    public void setStudent(StudentBean student) {this.student = student;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
}
