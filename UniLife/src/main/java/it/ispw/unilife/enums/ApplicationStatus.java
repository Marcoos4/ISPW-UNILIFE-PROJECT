package it.ispw.unilife.enums;

public enum ApplicationStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    ACCEPTED,
    REJECTED;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static ApplicationStatus fromString(String text) {
        if (text == null) return null;
        for (ApplicationStatus evaluation : ApplicationStatus.values()) {
            if (evaluation.name().equalsIgnoreCase(text.trim())) {
                return evaluation;
            }
        }
        return null;
    }
}
