package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AssignmentNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignmentName(null));
    }

    @Test
    public void constructor_invalidAssignmentName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AssignmentName(""));
        assertThrows(IllegalArgumentException.class, () -> new AssignmentName("Test#"));
        assertThrows(IllegalArgumentException.class, () -> new AssignmentName("Quiz!1"));
    }

    @Test
    public void isValidAssignmentName() {
        // null
        assertFalse(AssignmentName.isValidAssignmentName(null));

        // invalid — blank or empty
        assertFalse(AssignmentName.isValidAssignmentName("")); // empty string
        assertFalse(AssignmentName.isValidAssignmentName(" ")); // spaces only

        // invalid — special characters
        assertFalse(AssignmentName.isValidAssignmentName("Test#")); // hash
        assertFalse(AssignmentName.isValidAssignmentName("Quiz!1")); // exclamation
        assertFalse(AssignmentName.isValidAssignmentName("Lab@2")); // at sign
        assertFalse(AssignmentName.isValidAssignmentName("#Assignment")); // starts with special char


        // valid
        assertTrue(AssignmentName.isValidAssignmentName("Quiz")); // single word
        assertTrue(AssignmentName.isValidAssignmentName("Quiz 1")); // with space
        assertTrue(AssignmentName.isValidAssignmentName("Midterm Exam")); // multiple words
        assertTrue(AssignmentName.isValidAssignmentName("Assignment1")); // alphanumeric no space
        assertTrue(AssignmentName.isValidAssignmentName("Lab 2B")); // mixed alphanumeric with space
        assertTrue(AssignmentName.isValidAssignmentName("12345")); // numbers only
        assertTrue(AssignmentName.isValidAssignmentName(" Assignment")); // starts with space
    }

    @Test
    public void equals() {
        AssignmentName assignmentName = new AssignmentName("Quiz 1");

        // same values -> returns true
        assertTrue(assignmentName.equals(new AssignmentName("Quiz 1")));

        // same object -> returns true
        assertTrue(assignmentName.equals(assignmentName));

        // case-insensitive -> returns true
        assertTrue(assignmentName.equals(new AssignmentName("QUIZ 1")));
        assertTrue(assignmentName.equals(new AssignmentName("quiz 1")));

        // null -> returns false
        assertFalse(assignmentName.equals(null));

        // different types -> returns false
        assertFalse(assignmentName.equals(5.0f));

        // different values -> returns false
        assertFalse(assignmentName.equals(new AssignmentName("Quiz 2")));
    }

    @Test
    public void hashCode_caseInsensitive() {
        // Same name with different casing should produce same hashCode
        AssignmentName lower = new AssignmentName("quiz 1");
        AssignmentName upper = new AssignmentName("QUIZ 1");
        assertEquals(lower.hashCode(), upper.hashCode());
    }
}
