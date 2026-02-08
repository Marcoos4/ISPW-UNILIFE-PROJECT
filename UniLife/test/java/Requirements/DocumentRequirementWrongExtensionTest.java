package Requirements;

import it.ispw.unilife.exception.RequirementValidateException;
import it.ispw.unilife.model.Document;
import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.admission.DocumentRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentRequirementWrongExtensionTest {

    @Test
    void testValidateDocumentWrongExtension() {
        // Arrange
        // Requisito: Richiede "pdf"
        DocumentRequirement req = new DocumentRequirement("TRANSCRIPT", "Transcript", "Grades", "pdf", 10, true);

        Document doc = new Document();
        doc.setFileType("png"); // Utente carica un PNG
        doc.setFileSize(500.0); // Size OK

        ApplicationItem item = new ApplicationItem("TRANSCRIPT", doc);

        // Act & Assert
        RequirementValidateException exception = assertThrows(RequirementValidateException.class, () -> {
            req.validate(item);
        });

        assertEquals("Invalid file extension for 'TRANSCRIPT'. Expected: pdf, Found: png", exception.getMessage());
    }
}