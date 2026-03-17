package seedu.address.model.classspace.exceptions;

/**
 * Signals that the operation will result in duplicate class spaces.
 */
public class DuplicateClassSpaceException extends RuntimeException {
    public DuplicateClassSpaceException() {
        super("Operation would result in duplicate class spaces"); // This String is not user-facing.
    }
}
