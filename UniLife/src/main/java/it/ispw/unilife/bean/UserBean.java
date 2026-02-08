package it.ispw.unilife.bean;

public class UserBean {

    private String userName;
    private String name;
    private String surname;
    private String password;
    private String role;
    private String university;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPassword() { return password; }
    public void setPassword(String password) {this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUniversity() { return university; }

    public void setUniversity(String university) {this.university = university;}
}
