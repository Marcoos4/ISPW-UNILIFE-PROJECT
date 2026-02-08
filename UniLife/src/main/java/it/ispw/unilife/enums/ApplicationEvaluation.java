package it.ispw.unilife.enums;

public enum ApplicationEvaluation {
    ACCEPTED,
    REJECTED;

    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static ApplicationEvaluation fromString(String text) {
        if (text == null) return null;
        for (ApplicationEvaluation evaluation : ApplicationEvaluation.values()) {
            if (evaluation.name().equalsIgnoreCase(text.trim())) {
                return evaluation;
            }
        }
        return null;
    }


}
