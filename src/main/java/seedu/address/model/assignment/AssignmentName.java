package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;

/**
 * Represents an Assignment's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAssignmentName(String)}.
 */
public class AssignmentName {

    public static final String MESSAGE_CONSTRAINTS =
            "Assignment names should only contain alphanumeric characters and spaces, and should not be blank.";

    private static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String value;

    /**
     * Constructs an {@code AssignmentName}.
     *
     * @param assignmentName A valid assignment name.
     */
    public AssignmentName(String assignmentName) {
        requireNonNull(assignmentName);
        String trimmedAssignmentName = assignmentName.trim();
        if (!isValidAssignmentName(trimmedAssignmentName)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        value = trimmedAssignmentName;
    }

    /**
     * Returns true if a given string is a valid assignment name.
     */
    public static boolean isValidAssignmentName(String test) {
        return test != null && test.trim().matches(VALIDATION_REGEX);
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

        if (!(other instanceof AssignmentName)) {
            return false;
        }

        AssignmentName otherAssignmentName = (AssignmentName) other;
        return value.equalsIgnoreCase(otherAssignmentName.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }
}
