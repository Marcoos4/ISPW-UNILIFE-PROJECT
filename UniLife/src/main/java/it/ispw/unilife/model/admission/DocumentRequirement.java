package it.ispw.unilife.model.admission;

import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.RequirementValidateException;
import it.ispw.unilife.model.Document;

public class DocumentRequirement extends AbstractRequirement {
    private String allowedExtension;
    private double maxSizeMB;
    private final boolean isCertificate;

    public DocumentRequirement(String key, String label, String description,
                               String extension, long maxSizeMB, boolean isCertificate) {
        super(key, label, description);
        this.allowedExtension = extension;
        this.maxSizeMB = maxSizeMB;
        this.isCertificate = isCertificate;
    }

    @Override
    public void validate(ApplicationItem item) throws RequirementValidateException, DAOException {
        if (item == null || item.getDocumentContent() == null) {
            throw new DAOException("Document is missing for " + this.getLabel());
        }

        Document doc = item.getDocumentContent();

        if ((doc.getFileSize() / 1024.0) > this.maxSizeMB) {
            throw new RequirementValidateException("File " + this.getLabel() + " is over max size (" + this.maxSizeMB + "MB).");
        }

        String fileType = doc.getFileType();
        if (fileType == null || !fileType.equalsIgnoreCase(allowedExtension)) {
            // Messaggio più chiaro: mostra cosa è stato caricato vs cosa era atteso
            throw new RequirementValidateException("Invalid file extension for '" + this.getLabel() +
                    "'. Expected: " + allowedExtension + ", Found: " + fileType);
        }
    }

    // ... Getters e Setters invariati ...
    @Override
    public RequirementType getRequirementType() { return RequirementType.DOCUMENT; }

    public String getAllowedExtension() { return allowedExtension; }
    public void setAllowedExtensions(String allowedExtension) { this.allowedExtension = allowedExtension; }

    public double getMaxSizeMB() { return maxSizeMB; }
    public void setMaxSizeMB(long maxSizeMB) { this.maxSizeMB = maxSizeMB; }

    public boolean isCertificate() { return isCertificate; }
}