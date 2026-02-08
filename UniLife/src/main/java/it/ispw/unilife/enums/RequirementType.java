package it.ispw.unilife.enums;

public enum RequirementType {
    DOCUMENT,
    TEXT;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static RequirementType fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        for (RequirementType req : RequirementType.values()) {
            if (req.name().equalsIgnoreCase(value.trim())) {
                return req;
            }
        }
        return null;
    }
}
