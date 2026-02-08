package Requirements;

import it.ispw.unilife.exception.RequirementValidateException;
import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.admission.TextRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextRequirementTooShortTest {

    @Test
    void testValidateTextTooShort() {
        // Arrange
        TextRequirement req = new TextRequirement("BIO", "Biography", "Your bio", 20, 100);

        String shortText = "Too short"; // 9 caratteri
        ApplicationItem item = new ApplicationItem("BIO", shortText);

        // Act & Assert
        RequirementValidateException exception = assertThrows(RequirementValidateException.class, () -> {
            req.validate(item);
        });

        assertTrue(exception.getMessage().contains("Testo troppo corto"),
                "Message too short.");
    }
}