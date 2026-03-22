package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, Email.EMPTY_EMAIL_MESSAGE, () -> new Email(invalidEmail));
    }

    @Test
    public void getDiagnosticMessage_invalidEmails_returnsSpecificErrorMessage() {
        // Missing '@' symbol
        assertEquals(Email.MISSING_AT_SYMBOL_MESSAGE, Email.getDiagnosticMessage("peterjackexample.com"));

        // Multiple '@' symbols
        assertEquals(Email.MULTIPLE_AT_SYMBOL_MESSAGE, Email.getDiagnosticMessage("peter@jack@example.com"));

        // Missing local part
        assertEquals(Email.MESSAGE_MISSING_LOCAL_PART, Email.getDiagnosticMessage("@example.com"));

        // Local part starts with invalid character
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_START, "-"),
                Email.getDiagnosticMessage("-peterjack@example.com"));
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_START, " "),
                Email.getDiagnosticMessage(" peterjack@example.com"));

        // Local part ends with invalid character
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_END, "-"),
                Email.getDiagnosticMessage("peterjack-@example.com"));

        // Local part contains invalid characters (space, consecutive special characters, disallowed symbols)
        assertEquals(Email.MESSAGE_LOCAL_PART_INVALID_CHARS, Email.getDiagnosticMessage("peter jack@example.com"));
        assertEquals(Email.MESSAGE_LOCAL_PART_INVALID_CHARS, Email.getDiagnosticMessage("peter..jack@example.com"));
        assertEquals(Email.MESSAGE_LOCAL_PART_INVALID_CHARS, Email.getDiagnosticMessage("peter!jack@example.com"));

        // Missing domain
        assertEquals(Email.MESSAGE_MISSING_DOMAIN, Email.getDiagnosticMessage("peterjack@"));

        // Domain TLD too short
        assertEquals(Email.MESSAGE_DOMAIN_TLD_SHORT, Email.getDiagnosticMessage("peterjack@example.c"));

        // Domain contains consecutive periods
        assertEquals(Email.MESSAGE_DOMAIN_CONSECUTIVE_PERIODS, Email.getDiagnosticMessage("peterjack@example..com"));

        // Domain label invalid (starts/ends with hyphen, contains invalid symbols)
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "-example"),
                Email.getDiagnosticMessage("peterjack@-example.com"));
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "example-"),
                Email.getDiagnosticMessage("peterjack@example-.com"));
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "exam_ple"),
                Email.getDiagnosticMessage("peterjack@exam_ple.com"));

        // Email too long
        String longLocalPart = "a".repeat(311); // 311 + @gmail.com = 321 char.
        assertEquals(Email.MESSAGE_EMAIL_TOO_LONG, Email.getDiagnosticMessage(longLocalPart + "@gmail.com"));
    }


    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
