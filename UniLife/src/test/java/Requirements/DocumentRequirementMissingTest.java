package Requirements;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.admission.DocumentRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentRequirementMissingTest {

    @Test
    void testValidateDocumentMissing() {

        DocumentRequirement req = new DocumentRequirement("CERT", "Certificate", "English Cert", "pdf", 2, true);


        ApplicationItem item = new ApplicationItem("CERT", (String) null);


        DAOException exception = assertThrows(DAOException.class, () -> {
            req.validate(item);
        });

        assertTrue(exception.getMessage().contains("Document is missing"),
                "Should throw DAO EXCEPTION");
    }
}