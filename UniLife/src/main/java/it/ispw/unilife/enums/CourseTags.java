package it.ispw.unilife.enums;

public enum CourseTags {
    MATH,
    SCIENCE,
    ARTS,
    COMPUTER_SCIENCE,
    MEDICINE;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static CourseTags fromString(String text) {
        if (text == null) return null;
        for (CourseTags tag : CourseTags.values()) {
            if (tag.name().equalsIgnoreCase(text.trim())) {
                return tag;
            }
        }
        return null;
    }
}