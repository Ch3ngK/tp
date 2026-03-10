package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's matriculation number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidMatricNumber(String)}
 */
public class MatricNumber {

    public static final String MESSAGE_CONSTRAINTS = "Matriculation numbers should start with `A`,"
            + " followed by 7 digits and end with a capital letter.";

    /*
     * The first character of the matriculation number must be the alphabet 'A'.
     */
    public static final String VALIDATION_REGEX = "^[aA]\\d{7}[a-zA-Z]$";

    public final String value;

    /**
     * Constructs a {@code MatricNumber}.
     *
     * @param matricNumber A valid matriculation number.
     */
    public MatricNumber(String matricNumber) {
        requireNonNull(matricNumber);
        checkArgument(isValidMatricNumber(matricNumber), MESSAGE_CONSTRAINTS);
        value = matricNumber;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidMatricNumber(String test) {
        return test.matches(VALIDATION_REGEX);
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
