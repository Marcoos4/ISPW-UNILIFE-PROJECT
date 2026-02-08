package it.ispw.unilife.enums;

public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    PAYED,
    COMPLETED;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static ReservationStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return null;
    }
}
