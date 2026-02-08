package it.ispw.unilife.bean;

public class EvaluationContextBean {
    private ApplicationBean applicationBean;
    private NotificationBean notificationBean;

    public EvaluationContextBean(ApplicationBean applicationBean, NotificationBean notificationBean) {
        this.applicationBean = applicationBean;
        this.notificationBean = notificationBean;
    }

    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }

    public NotificationBean getNotificationBean() {
        return notificationBean;
    }
}