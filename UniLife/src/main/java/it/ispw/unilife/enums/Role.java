package it.ispw.unilife.enums;

public enum Role {
    STUDENT,
    TUTOR,
    UNIVERSITY_EMPLOYEE;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static Role fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        return null;
    }
}
