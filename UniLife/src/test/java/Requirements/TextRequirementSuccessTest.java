package Requirements;

import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.admission.TextRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TextRequirementSuccessTest {

    @Test
    void testValidateTextSuccess() {
        // Arrange
        // Requisito: Minimo 10 caratteri, Massimo 50
        TextRequirement req = new TextRequirement("MOTIVATION", "Motivation Letter", "Why join?", 10, 50);

        String validText = "I really want to join this university."; // 38 caratteri
        ApplicationItem item = new ApplicationItem("MOTIVATION", validText);

        // Act & Assert
        assertDoesNotThrow(() -> req.validate(item),
                "Validate thow.");
    }
}