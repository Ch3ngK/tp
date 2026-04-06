package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        // EP: null name passed to constructor -> throws NullPointerException
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        // EP: invalid name passed to constructor -> throws IllegalArgumentException
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // EP: null name -> throws NullPointerException
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // BVA: empty string
        assertFalse(Name.isValidName(" ")); // EP: spaces only
        assertFalse(Name.isValidName("^")); // EP: only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // EP: contains non-alphanumeric characters
        assertFalse(Name.isValidName("a".repeat(301))); // BVA: exceeds 300 characters

        // valid name
        assertTrue(Name.isValidName("peter jack")); // EP: alphabets only
        assertTrue(Name.isValidName("12345")); // EP: numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // EP: alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // EP: with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // EP: long names
        assertTrue(Name.isValidName("a".repeat(300))); // BVA: exactly 300 characters
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // EP: same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // EP: same object -> returns true
        assertTrue(name.equals(name));

        // EP: null -> returns false
        assertFalse(name.equals(null));

        // EP: different types -> returns false
        assertFalse(name.equals(5.0f));

        // EP: different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
