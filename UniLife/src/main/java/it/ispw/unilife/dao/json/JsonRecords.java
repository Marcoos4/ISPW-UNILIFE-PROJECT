package it.ispw.unilife.dao.json;

import java.time.LocalDateTime;

public class JsonRecords {

    private JsonRecords() {}

    public static class UniversityRecord {
        private String name;
        private String location;
        private int ranking;
        private double livingCost;

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

        public double getLivingCost() {
            return livingCost;
        }

        public void setLivingCost(double livingCost) {
            this.livingCost = livingCost;
        }
    }

    public static class UserRecord {
        private String username;
        private String name;
        private String surname;
        private String password;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class StudentRecord {
        private String username;
        private String name;
        private String surname;
        private String password;
        private double budget;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public double getBudget() {
            return budget;
        }

        public void setBudget(double budget) {
            this.budget = budget;
        }
    }

    public static class TutorRecord {
        private String username;
        private String name;
        private String surname;
        private String password;
        private float rating;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }
    }

    public static class UniversityEmployeeRecord {
        private String username;
        private String name;
        private String surname;
        private String password;
        private String universityName;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }
    }

    public static class CourseRecord {
        private String title;
        private String description;
        private String universityName;
        private int duration;
        private double fees;
        private String courseType;
        private String language;

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

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
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

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public static class CourseTagRecord {
        private String courseTitle;
        private String universityName;
        private String tag;

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    public static class DocumentRequirementRecord {
        private String courseTitle;
        private String universityName;
        private String name;
        private double maxSize;
        private String allowedExtension;
        private boolean isCertificate;
        private String label;
        private String description;

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(double maxSize) {
            this.maxSize = maxSize;
        }

        public String getAllowedExtension() {
            return allowedExtension;
        }

        public void setAllowedExtension(String allowedExtension) {
            this.allowedExtension = allowedExtension;
        }

        public boolean isCertificate() {
            return isCertificate;
        }

        public void setCertificate(boolean certificate) {
            isCertificate = certificate;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class TextRequirementRecord {
        private String courseTitle;
        private String universityName;
        private String name;
        private int minChar;
        private int maxChar;
        private String label;
        private String description;

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMinChar() {
            return minChar;
        }

        public void setMinChar(int minChar) {
            this.minChar = minChar;
        }

        public int getMaxChar() {
            return maxChar;
        }

        public void setMaxChar(int maxChar) {
            this.maxChar = maxChar;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class DocumentRecord {
        private String name;
        private String type;
        private double size;
        private String contentBase64;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }

        public String getContentBase64() {
            return contentBase64;
        }

        public void setContentBase64(String contentBase64) {
            this.contentBase64 = contentBase64;
        }
    }

    public static class ApplicationRecord {
        private String courseTitle;
        private String universityName;
        private LocalDateTime creationDate;
        private String studentUsername;
        private LocalDateTime submissionDate;
        private String status;

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }

        public LocalDateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
        }

        public String getStudentUsername() {
            return studentUsername;
        }

        public void setStudentUsername(String studentUsername) {
            this.studentUsername = studentUsername;
        }

        public LocalDateTime getSubmissionDate() {
            return submissionDate;
        }

        public void setSubmissionDate(LocalDateTime submissionDate) {
            this.submissionDate = submissionDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ApplicationItemRecord {
        private String courseTitle;
        private String universityName;
        private LocalDateTime creationDate;
        private String studentUsername;
        private String requirementName;
        private String type;
        private String text;
        private String document;

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }

        public LocalDateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
        }

        public String getStudentUsername() {
            return studentUsername;
        }

        public void setStudentUsername(String studentUsername) {
            this.studentUsername = studentUsername;
        }

        public String getRequirementName() {
            return requirementName;
        }

        public void setRequirementName(String requirementName) {
            this.requirementName = requirementName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }
    }

    public static class LessonRecord {
        private String subject;
        private float price;
        private LocalDateTime start;
        private LocalDateTime end;
        private int duration;
        private String tutorUsername;
        private String status;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getTutorUsername() {
            return tutorUsername;
        }

        public void setTutorUsername(String tutorUsername) {
            this.tutorUsername = tutorUsername;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ReservationRecord {
        private String studentUsername;
        private String tutorUsername;
        private LocalDateTime startDateTime;
        private String status;
        private String paymentStripe;

        public String getStudentUsername() {
            return studentUsername;
        }

        public void setStudentUsername(String studentUsername) {
            this.studentUsername = studentUsername;
        }

        public String getTutorUsername() {
            return tutorUsername;
        }

        public void setTutorUsername(String tutorUsername) {
            this.tutorUsername = tutorUsername;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaymentStripe() {
            return paymentStripe;
        }

        public void setPaymentStripe(String paymentStripe) {
            this.paymentStripe = paymentStripe;
        }
    }

    public static class PaymentRecord {
        private float amount;
        private String status;
        private String paymentStripe;

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaymentStripe() {
            return paymentStripe;
        }

        public void setPaymentStripe(String paymentStripe) {
            this.paymentStripe = paymentStripe;
        }
    }

    public static class InterestedStudentRecord {
        private String username;
        private String courseName;
        private String universityName;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }
    }

    public static class StudentInterestRecord {
        private String studentUsername;
        private String tag;

        public String getStudentUsername() {
            return studentUsername;
        }

        public void setStudentUsername(String studentUsername) {
            this.studentUsername = studentUsername;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    public static class ReservationNotificationRecord {
        private String username;
        private LocalDateTime timestamp;
        private String status;
        private String message;
        private String studentUsername;
        private String tutorUsername;
        private LocalDateTime start;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStudentUsername() {
            return studentUsername;
        }

        public void setStudentUsername(String studentUsername) {
            this.studentUsername = studentUsername;
        }

        public String getTutorUsername() {
            return tutorUsername;
        }

        public void setTutorUsername(String tutorUsername) {
            this.tutorUsername = tutorUsername;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }
    }

    public static class ApplicationNotificationRecord {
        private String username;
        private LocalDateTime timestamp;
        private String status;
        private String message;
        private String courseTitle;
        private String universityName;
        private String studentUsername;
        private LocalDateTime creationDate;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getUniversityName() {
            return universityName;
        }

        public void setUniversityName(String universityName) {
            this.universityName = universityName;
        }

        public String getStudentUsername() {
            return studentUsername;
        }

        public void setStudentUsername(String studentUsername) {
            this.studentUsername = studentUsername;
        }

        public LocalDateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
        }
    }

    public static class LessonNotificationRecord {
        private String username;
        private LocalDateTime timestamp;
        private String status;
        private String message;
        private LocalDateTime start;
        private String tutorUsername;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public String getTutorUsername() {
            return tutorUsername;
        }

        public void setTutorUsername(String tutorUsername) {
            this.tutorUsername = tutorUsername;
        }
    }

    public static class UserNotificationRecord {
        private String username;
        private String senderUsername;
        private LocalDateTime timestamp;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSenderUsername() {
            return senderUsername;
        }

        public void setSenderUsername(String senderUsername) {
            this.senderUsername = senderUsername;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}