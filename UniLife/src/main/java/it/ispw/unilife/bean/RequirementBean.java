package it.ispw.unilife.bean;

public class RequirementBean {
    private String name;
    private String label;
    private String description;
    private boolean certificate;
    private String type;        // "TEXT" or "DOCUMENT"

    // TEXT
    private int minChars;
    private int maxChars;

    // DOCUMENT
    private String allowedExtension;
    private double maxSizeMB;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCertificate() { return certificate; }
    public void setCertificate(boolean certificate) { this.certificate = certificate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }


    public int getMinChars() { return minChars; }
    public void setMinChars(int minChars) { this.minChars = minChars; }

    public int getMaxChars() { return maxChars; }
    public void setMaxChars(int maxChars) { this.maxChars = maxChars; }


    public String getAllowedExtension() { return allowedExtension; }
    public void setAllowedExtension(String allowedExtension) { this.allowedExtension = allowedExtension; }

    public double getMaxSizeMB() { return maxSizeMB; }
    public void setMaxSizeMB(double maxSizeMB) { this.maxSizeMB = maxSizeMB; }
}
