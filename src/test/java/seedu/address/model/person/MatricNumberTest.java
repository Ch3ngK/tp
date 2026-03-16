package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MatricNumberTest {

    private static final String VALID_MATRIC_NUMBER_1 = "A1111111M";
    private static final String VALID_MATRIC_NUMBER_1_LOWERCASE = "a1111111m";
    private static final String VALID_MATRIC_NUMBER_2 = "A4455667L";

    private static final String INVALID_MATRIC_NUMBER_WRONG_FORMAT_START = "B1234567M";
    private static final String INVALID_MATRIC_NUMBER_WRONG_CHECKSUM = "A1111111A";
    private static final char EXPECTED_CHECKSUM_FOR_INVALID_MATRIC_NUMBER = 'M';

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MatricNumber(null));
    }

    @Test
    public void constructor_invalidMatricNumber_throwsIllegalArgumentException() {
        String invalidMatricNumber = "";
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber(invalidMatricNumber));
    }

    @Test
    public void constructor_storesUpperCase_success() {
        MatricNumber matricNumber = new MatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE);
        assertEquals(VALID_MATRIC_NUMBER_1, matricNumber.value);
    }

    @Test
    public void constructor_invalidChecksum_throwsIllegalArgumentExceptionWithSpecificMessage() {
        String expectedMessage = String.format(MatricNumber.MESSAGE_INVALID_CHECKSUM,
                EXPECTED_CHECKSUM_FOR_INVALID_MATRIC_NUMBER);
        assertThrows(IllegalArgumentException.class,
                expectedMessage, () -> new MatricNumber(INVALID_MATRIC_NUMBER_WRONG_CHECKSUM));
    }

    @Test
    public void hasValidMatricNumber() {
        // null matriculation number
        assertThrows(NullPointerException.class, () -> MatricNumber.hasValidFormat(null));
        assertFalse(MatricNumber.hasValidMatricNumber(null));
        // invalid matriculation numbers
        assertFalse(MatricNumber.hasValidFormat("")); // empty string
        assertFalse(MatricNumber.hasValidFormat(" ")); // spaces only
        assertFalse(MatricNumber.hasValidFormat(INVALID_MATRIC_NUMBER_WRONG_FORMAT_START)); //starts with `B`
        assertFalse(MatricNumber.hasValidMatricNumber(INVALID_MATRIC_NUMBER_WRONG_FORMAT_START));
        assertFalse(MatricNumber.hasValidFormat("1234567")); //only numbers
        assertFalse(MatricNumber.hasValidFormat("A12345678M")); //has 8 digits
        assertFalse(MatricNumber.hasValidFormat("A123456Z")); //has 6 digits
        assertFalse(MatricNumber.hasValidFormat("A0N")); // 1 digit
        assertFalse(MatricNumber.hasValidFormat("AZ")); //no digits
        assertFalse(MatricNumber.hasValidFormat(VALID_MATRIC_NUMBER_1 + " ")); // trailing space
        assertFalse(MatricNumber.hasValidFormat(" " + VALID_MATRIC_NUMBER_1)); // leading space
        assertFalse(MatricNumber.hasValidFormat("A1234 567N")); // space in the middle


        // wrong checksums
        assertFalse(MatricNumber.hasValidMatricNumber("A1111111A")); // wrong checksum, should be 'M'
        assertFalse(MatricNumber.hasValidMatricNumber("A0388420K")); // wrong checksum, should be 'B'
        assertFalse(MatricNumber.hasValidMatricNumber("A2222222A")); // wrong checksum, should be 'B'

        // valid matriculation numbers
        assertTrue(MatricNumber.hasValidMatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE));
        assertTrue(MatricNumber.hasValidMatricNumber(VALID_MATRIC_NUMBER_1));
        assertTrue(MatricNumber.hasValidMatricNumber(VALID_MATRIC_NUMBER_2));
        assertTrue(MatricNumber.hasValidFormat("A4433221B"));
        assertTrue(MatricNumber.hasValidFormat("A0000000A")); // all same digits
        assertTrue(MatricNumber.hasValidFormat("a4455667L")); // starts with lower capital `a`
        assertTrue(MatricNumber.hasValidFormat("A4455667L")); // ends with lower capital
        assertTrue(MatricNumber.hasValidFormat("a4455667l")); // characters are both in lower capital
    }

    @Test
    public void hasValidMatricNumber_null_returnsFalse() {
        assertFalse(MatricNumber.hasValidMatricNumber(null));
    }

    @Test
    public void calculateChecksum_sumIsZero_triggersNegativeRemainderBlock() {
        assertTrue(MatricNumber.hasValidMatricNumber("A0000000Y"));
    }

    @Test
    public void validateMatricNumber_triggersLoggingMethods() {
        // Triggers logFormatError.
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber("B1234567A"));

        // Triggers logChecksumError.
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber("A1111111A"));
    }

    @Test
    public void equals() {
        MatricNumber matricNumber = new MatricNumber(VALID_MATRIC_NUMBER_1);

        // same values -> returns true
        assertTrue(matricNumber.equals(new MatricNumber(VALID_MATRIC_NUMBER_1)));

        //different case -> returns true
        assertTrue(matricNumber.equals(new MatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE)));

        // same object -> returns true
        assertTrue(matricNumber.equals(matricNumber));

        // null -> returns false
        assertFalse(matricNumber.equals(null));

        // different types -> returns false
        assertFalse(matricNumber.equals(5.0f));

        // different values -> returns false
        assertFalse(matricNumber.equals(new MatricNumber(VALID_MATRIC_NUMBER_2)));
    }

    @Test
    public void hashCode_test() {
        MatricNumber matricNumber1 = new MatricNumber(VALID_MATRIC_NUMBER_1);
        MatricNumber matricNumber2 = new MatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE);

        // same matric number, different case -> same hashcode
        assertEquals(matricNumber1.hashCode(), matricNumber2.hashCode());

        MatricNumber matricNumber3 = new MatricNumber(VALID_MATRIC_NUMBER_2);
        assertNotEquals(matricNumber1.hashCode(), matricNumber3.hashCode());
    }
}
