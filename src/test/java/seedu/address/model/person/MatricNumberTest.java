package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MatricNumberTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MatricNumber(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber(invalidAddress));
    }

    @Test
    public void isValidMatricNumber() {
        // null address
        assertThrows(NullPointerException.class, () -> MatricNumber.isValidMatricNumber(null));

        // invalid addresses
        assertFalse(MatricNumber.isValidMatricNumber("")); // empty string
        assertFalse(MatricNumber.isValidMatricNumber(" ")); // spaces only
        assertFalse(MatricNumber.isValidMatricNumber("B1234567N")); //starts with `B`
        assertFalse(MatricNumber.isValidMatricNumber("1234567")); //only numbers
        assertFalse(MatricNumber.isValidMatricNumber("A12345678Z")); //has 8 digits
        assertFalse(MatricNumber.isValidMatricNumber("A123456Z")); //has 6 digits
        assertFalse(MatricNumber.isValidMatricNumber("A0N")); // 1 digit
        assertFalse(MatricNumber.isValidMatricNumber("AZ")); //no digits

        // valid addresses
        assertTrue(MatricNumber.isValidMatricNumber("A4433221B"));
        assertTrue(MatricNumber.isValidMatricNumber("A0000000A")); // all same digits
        assertTrue(MatricNumber.isValidMatricNumber("A4455667A")); // ends with `A`
        assertTrue(MatricNumber.isValidMatricNumber("a4455667Z")); // starts with lower capital `a`
        assertTrue(MatricNumber.isValidMatricNumber("A4455667z")); // ends with lower capital
        assertTrue(MatricNumber.isValidMatricNumber("a4455667z")); // characters are both in lower capital
    }

    @Test
    public void equals() {
        MatricNumber matricNumber = new MatricNumber("A1111111Z");

        // same values -> returns true
        assertTrue(matricNumber.equals(new MatricNumber("A1111111Z")));

        //TODO: add case-insensitive test case

        // same object -> returns true
        assertTrue(matricNumber.equals(matricNumber));

        // null -> returns false
        assertFalse(matricNumber.equals(null));

        // different types -> returns false
        assertFalse(matricNumber.equals(5.0f));

        // different values -> returns false
        assertFalse(matricNumber.equals(new MatricNumber("A1111111X")));
    }

    @Test
    public void hashCode_test() {
        String validMatricNumber = "A1234567X";
        MatricNumber matricNumber1 = new MatricNumber(validMatricNumber);
        MatricNumber matricNumber2 = new MatricNumber(validMatricNumber);
        assertEquals(matricNumber1, matricNumber2);
    }
}
