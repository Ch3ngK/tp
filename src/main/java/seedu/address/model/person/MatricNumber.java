package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Represents a Person's matriculation number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidFormat(String)}
 */
public class MatricNumber {

    public static final int CHECKSUM_POSITION = 8;

    public static final String MESSAGE_CONSTRAINTS = "Matriculation numbers should start with `A`,"
            + " followed by 7 digits and end with a valid checksum letter.";

    public static final String MESSAGE_INVALID_CHECKSUM =
            "The matriculation number checksum letter is incorrect. For the given digits, it should be '%c'.";

    /*
     * The first character of the matriculation number must be the alphabet 'A'.
     */
    public static final String VALIDATION_REGEX = "^[aA]\\d{7}[a-zA-Z]$";

    private static final Logger logger = LogsCenter.getLogger(MatricNumber.class);

    public final String value;

    /**
     * Constructs a {@code MatricNumber}.
     *
     * @param matricNumber A valid matriculation number.
     */
    public MatricNumber(String matricNumber) {
        requireNonNull(matricNumber);
        String upperCaseMatricNumber = matricNumber.toUpperCase();
        validateMatricNumber(upperCaseMatricNumber);
        value = upperCaseMatricNumber;
        logCreationSuccess();
    }

    /**
     * Returns true if matriculation number has a valid format.
     *
     * @param matricNumber Matriculation number to be tested.
     * @return True if matriculation number is valid.
     */
    public static boolean isValidFormat(String matricNumber) {
        return matricNumber.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if matriculation number has a valid checksum letter and format.
     *
     * @param matricNumber Matriculation number to be tested.
     * @return True if matriculation number is valid.
     */
    public static boolean isValidMatricNumber(String matricNumber) {
        return matricNumber != null && isValidFormat(matricNumber) && hasCorrectChecksum(matricNumber);
    }

    private static boolean hasCorrectChecksum(String matricNumber) {
        return isChecksumValid(matricNumber);
    }

    private static boolean isChecksumValid(String matricNumber) {
        return calculateChecksum(matricNumber) == extractProvidedChecksum(matricNumber);
    }

    private static char extractProvidedChecksum(String matricNumber) {
        return matricNumber.charAt(CHECKSUM_POSITION);
    }

    private static String getChecksumErrorMessage(String matricNumber) {
        return String.format(MESSAGE_INVALID_CHECKSUM, calculateChecksum(matricNumber));
    }

    private static void validateMatricNumber(String matricNumber) {
        if (!isValidFormat(matricNumber)) {
            logFormatError(matricNumber);
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        if (!hasCorrectChecksum(matricNumber)) {
            logChecksumError(matricNumber);
            throw new IllegalArgumentException(getChecksumErrorMessage(matricNumber));
        }
    }
    /**
     * Calculates the checksum character for NUS matriculation numbers.
     *
     * @param matricNumber Matriculation number with "A" and 7 digits.
     * @return Checksum character for matriculation number.
     */
    private static char calculateChecksum(String matricNumber) {
        String checksumLetters = "BAEHJLMNRUWXY";
        int length = checksumLetters.length();
        int[] weights = {-1, -1, -1, -1, -1, -1};
        String digits = matricNumber.substring(2, 8);
        int sum = 0;

        for (int i = 0; i < digits.length(); i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * weights[i];
        }

        int remainder = (sum - 1) % length;
        if (remainder < 0) {
            remainder += length;
        }

        return checksumLetters.charAt(remainder);
    }

    private void logCreationSuccess() {
        logger.fine("Successfully created MatricNumber: " + value);
    }

    private static void logFormatError(String matricNumber) {
        logger.warning("Failed creation: Invalid format for MatricNumber: '" + matricNumber + "'");
    }

    private static void logChecksumError(String matricNumber) {
        logger.warning("Failed creation: Checksum mismatch for MatricNumber: '" + matricNumber + "'");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MatricNumber)) {
            return false;
        }

        MatricNumber otherMatricNumber = (MatricNumber) other;
        return value.equalsIgnoreCase(otherMatricNumber.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
