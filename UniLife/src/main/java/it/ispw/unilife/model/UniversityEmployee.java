package it.ispw.unilife.model;

import it.ispw.unilife.enums.Role;

public class UniversityEmployee extends User{

    private University university;

    public UniversityEmployee(University university) {
        super();
        this.role = Role.UNIVERSITY_EMPLOYEE;
        this.university = university;
    }

    public UniversityEmployee(String username, String name, String surname, String password) {
        super(username, name, surname, password, Role.UNIVERSITY_EMPLOYEE);
    }

    public UniversityEmployee(String username, String name, String surname, String password, University university) {
        super(username, name, surname, password, Role.UNIVERSITY_EMPLOYEE);
        this.university = university;
    }

    public University getUniversity() {
        return university;
    }
    public void setUniversity(University university) {
        this.university = university;
    }

}
