package seedu.address.model.classspace;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a class space in the address book.
 */
public class ClassSpace {
    private final ClassSpaceName classSpaceName;

    /**
     * Creates a {@code ClassSpace} with the given name.
     *
     * @param classSpaceName Name of the class space.
     */
    public ClassSpace(ClassSpaceName classSpaceName) {
        requireNonNull(classSpaceName);
        this.classSpaceName = classSpaceName;
    }

    public ClassSpaceName getClassSpaceName() {
        return classSpaceName;
    }

    /**
     * Returns true if both class spaces have the same identity.
     */
    public boolean isSameClassSpace(ClassSpace otherClassSpace) {
        if (otherClassSpace == this) {
            return true;
        }

        return otherClassSpace != null
                && classSpaceName.equals(otherClassSpace.classSpaceName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ClassSpace)) {
            return false;
        }

        ClassSpace otherClassSpace = (ClassSpace) other;
        return classSpaceName.equals(otherClassSpace.classSpaceName);
    }

    @Override
    public int hashCode() {
        return classSpaceName.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classSpaceName", classSpaceName)
                .toString();
    }
}
