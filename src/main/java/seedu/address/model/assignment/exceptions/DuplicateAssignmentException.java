package seedu.address.model.assignment.exceptions;

/**
 * Signals that the operation will result in duplicate assignments.
 */
public class DuplicateAssignmentException extends RuntimeException {
    public DuplicateAssignmentException() {
        super("Operation would result in duplicate assignments");
    }
}
