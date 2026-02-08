package it.ispw.unilife.enums;

public enum PaymentStatus {
    UNPAID,
    PAID,
    FAILED;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static PaymentStatus fromString(String text) {
        if (text == null) return null;
        for (PaymentStatus b : PaymentStatus.values()) {
            if (b.name().equalsIgnoreCase(text.trim())) {
                return b;
            }
        }
        return null;
    }
}
