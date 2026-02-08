package Requirements;

import it.ispw.unilife.model.Document;
import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.admission.DocumentRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DocumentRequirementSuccessTest {

    @Test
    void testValidateDocumentSuccess() {
        // Arrange
        // Requisito: PDF, Max 5 MB
        DocumentRequirement req = new DocumentRequirement("CV", "Curriculum", "Upload CV", "pdf", 5, false);

        Document doc = new Document();
        doc.setFileType("pdf");
        doc.setFileSize(2048.0); // 2048 KB = 2 MB (Sotto il limite di 5 MB)

        ApplicationItem item = new ApplicationItem("CV", doc);

        // Act & Assert
        assertDoesNotThrow(() -> req.validate(item),
                "Should not throw exception");
    }
}