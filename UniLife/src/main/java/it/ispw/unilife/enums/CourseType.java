package it.ispw.unilife.enums;

public enum CourseType {
    UNDERGRADUATE,
    POSTGRADUATE,
    PHD;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static CourseType fromString(String text) {
        if (text == null) return null;
        for (CourseType b : CourseType.values()) {
            if (b.name().equalsIgnoreCase(text.trim())) {
                return b;
            }
        }
        return null;
    }
}