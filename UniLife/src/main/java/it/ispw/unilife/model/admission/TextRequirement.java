package it.ispw.unilife.model.admission;

import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.RequirementValidateException;

public class TextRequirement extends AbstractRequirement {

    private int minChars;
    private int maxChars;

    public TextRequirement(String name, String label, String description,
                           int minChars, int maxChars) {
        super(name != null ? name.toUpperCase() : "",
                label != null ? label.toUpperCase() : "",
                description != null ? description.toUpperCase() : "");
        this.minChars = minChars;
        this.maxChars = maxChars;
    }

    @Override
    public void validate(ApplicationItem item) throws DAOException, RequirementValidateException {
        String text = (item != null) ? item.getTextContent() : "";

        if (text.length() < minChars) throw new RequirementValidateException("Testo troppo corto per " + this.getLabel());
        if (text.length() > maxChars) throw new RequirementValidateException("Testo troppo lungo per " + this.getLabel());
    }

    @Override
    public RequirementType getRequirementType() {
        return RequirementType.TEXT;
    }

    public int getMaxChars() { return maxChars; }
    public int getMinChars() { return minChars; }

    public void setMinChars(int minChars) { this.minChars = minChars; }
    public void setMaxChars(int maxChars) { this.maxChars = maxChars; }
}
