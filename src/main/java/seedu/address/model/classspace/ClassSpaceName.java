package seedu.address.model.classspace;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the name of a class space.
 * Guarantees: immutable; is valid as declared in {@link #isValidClassSpaceName(String)}.
 */
public class ClassSpaceName {

    public static final String MESSAGE_CONSTRAINTS = "Group names should only contain letters, numbers, "
            + "spaces, hyphens, and underscores, and it should not be blank.";
    private static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} _-]*";

    public final String value;

    /**
     * Constructs a {@code ClassSpaceName}.
     */
    public ClassSpaceName(String name) {
        requireNonNull(name);
        String trimmedName = name.trim();
        checkArgument(isValidClassSpaceName(trimmedName), MESSAGE_CONSTRAINTS);
        value = trimmedName;
    }

    /**
     * Returns true if a given string is a valid class space name.
     */
    public static boolean isValidClassSpaceName(String test) {
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

        if (!(other instanceof ClassSpaceName)) {
            return false;
        }

        ClassSpaceName otherName = (ClassSpaceName) other;
        return value.equalsIgnoreCase(otherName.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }
}
