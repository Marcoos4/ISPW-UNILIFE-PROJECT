package it.ispw.unilife.enums;

public enum LessonStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static LessonStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        for (LessonStatus status : LessonStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return null;
    }
}