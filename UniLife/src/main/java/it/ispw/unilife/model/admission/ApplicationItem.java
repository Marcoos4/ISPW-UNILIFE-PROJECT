package it.ispw.unilife.model.admission;

import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.model.Document;

public class ApplicationItem {

    private final String requirementName;
    private RequirementType type;
    private String textContent;
    private Document documentContent;

    public ApplicationItem(String requirementName, RequirementType type, String textContent, Document documentContent) {
        this.requirementName = requirementName;
        this.type = type;
        this.textContent = textContent;
        this.documentContent = documentContent;
    }

    public ApplicationItem(String reqName, String text) {
        this.requirementName = reqName;
        this.type = RequirementType.TEXT;
        this.textContent = text;
    }


    public ApplicationItem(String reqName, Document doc) {
        this.requirementName = reqName;
        this.type = RequirementType.DOCUMENT;
        this.documentContent = doc;
    }

    public String getRequirementName() { return requirementName; }
    public RequirementType getType() { return type; }
    public String getTextContent() { return textContent; }
    public Document getDocumentContent() { return documentContent; }
}
