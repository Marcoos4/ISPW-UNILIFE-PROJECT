package it.ispw.unilife.enums;

public enum NotificationStatus {
    PENDING,
    COMPLETED;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static NotificationStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        for (NotificationStatus status : NotificationStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return null;
    }
}
