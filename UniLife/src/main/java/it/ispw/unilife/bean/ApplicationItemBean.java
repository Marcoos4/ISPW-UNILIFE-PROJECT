package it.ispw.unilife.bean;

public class ApplicationItemBean {

    private String requirementName;
    private String type;

    private String textContent;
    private DocumentBean document;

    public String getRequirementName() { return requirementName; }
    public void setRequirementName(String requirementName) { this.requirementName = requirementName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public DocumentBean getDocument() { return document; }
    public void setDocument(DocumentBean document) { this.document = document; }
}