package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's attendance status in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAttendance(String)}
 */
public class Attendance {

    public static final String MESSAGE_CONSTRAINTS =
            "Attendance must be one of the following values: PRESENT, ABSENT, UNSET.";

    /**
     * The allowed attendance states.
     */
    public enum Status {
        PRESENT,
        ABSENT,
        UNINITIALISED
    }

    public final Status value;

    /**
     * Constructs an {@code Attendance}.
     *
     * @param attendance A valid attendance status.
     */
    public Attendance(Status attendance) {
        requireNonNull(attendance);
        value = attendance;
    }

    /**
     * Parses a string into a valid attendance status.
     */
    public Attendance(String attendance) {
        requireNonNull(attendance);
        String normalized = attendance.trim().toUpperCase();
        checkArgument(isValidAttendance(normalized), MESSAGE_CONSTRAINTS);
        value = Status.valueOf(normalized);
    }

    /**
     * Returns true if a given string is a valid attendance value.
     */
    public static boolean isValidAttendance(String test) {
        requireNonNull(test);
        try {
            Status status = Status.valueOf(test.trim().toUpperCase());
            //return status != Status.UNINITIALISED;
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Attendance otherAttendance)) {
            return false;
        }

        return value.equals(otherAttendance.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
