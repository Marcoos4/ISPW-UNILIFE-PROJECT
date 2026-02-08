package it.ispw.unilife.bean;

public class CertificateValidationBean {
    private DocumentBean document;
    private boolean  valid;

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public DocumentBean getDocument() { return document; }
    public void setDocument(DocumentBean document) { this.document = document; }
}
