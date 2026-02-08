package Requirements;

import it.ispw.unilife.exception.RequirementValidateException;
import it.ispw.unilife.model.Document;
import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.admission.DocumentRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentRequirementSizeExceededTest {

    @Test
    void testValidateDocumentSizeExceeded() {
        // Arrange
        // Requisito: Max 1 MB
        DocumentRequirement req = new DocumentRequirement("ID", "Identity Card", "Upload ID", "jpg", 1, false);

        Document doc = new Document();
        doc.setFileType("jpg");
        // Impostiamo 1.5 MB in KB -> 1.5 * 1024 = 1536 KB
        doc.setFileSize(1536.0);

        ApplicationItem item = new ApplicationItem("ID", doc);

        // Act & Assert
        RequirementValidateException exception = assertThrows(RequirementValidateException.class, () -> {
            req.validate(item);
        });

        assertTrue(exception.getMessage().contains("over max size"),
                "SHOULD notify over max size");
    }
}