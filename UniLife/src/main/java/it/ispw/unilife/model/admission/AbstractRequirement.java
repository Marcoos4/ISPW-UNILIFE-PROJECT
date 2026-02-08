package it.ispw.unilife.model.admission;

import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.RequirementValidateException;

public abstract class AbstractRequirement {
    protected String name;
    protected String label;
    protected String description;

    protected AbstractRequirement(String name, String label, String description) {
        this.name = name;
        this.label = label;
        this.description = description;
    }

    public abstract void validate(ApplicationItem item) throws DAOException, RequirementValidateException;

    public abstract RequirementType getRequirementType();

    public String getLabel() { return label; }
    public String getDescription() { return description; }

    public String getName() { return name; }
}
