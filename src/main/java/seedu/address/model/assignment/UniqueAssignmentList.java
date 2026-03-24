package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.assignment.exceptions.AssignmentNotFoundException;
import seedu.address.model.assignment.exceptions.DuplicateAssignmentException;

/**
 * A list of assignments that enforces uniqueness between its elements and does not allow nulls.
 */
public class UniqueAssignmentList implements Iterable<Assignment> {

    private final ObservableList<Assignment> internalList = FXCollections.observableArrayList();
    private final ObservableList<Assignment> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent assignment as the given argument.
     */
    public boolean contains(Assignment toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameAssignment);
    }

    /**
     * Returns the assignment with the given name if it exists.
     */
    public Optional<Assignment> findAssignmentByName(AssignmentName assignmentName) {
        requireNonNull(assignmentName);
        return internalList.stream()
                .filter(assignment -> assignment.getAssignmentName().equals(assignmentName))
                .findFirst();
    }

    /**
     * Adds an assignment to the list.
     */
    public void add(Assignment toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateAssignmentException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the assignment {@code target} in the list with {@code editedAssignment}.
     */
    public void setAssignment(Assignment target, Assignment editedAssignment) {
        requireAllNonNull(target, editedAssignment);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new AssignmentNotFoundException();
        }

        if (!target.isSameAssignment(editedAssignment) && contains(editedAssignment)) {
            throw new DuplicateAssignmentException();
        }

        internalList.set(index, editedAssignment);
    }

    /**
     * Removes the equivalent assignment from the list.
     */
    public void remove(Assignment toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new AssignmentNotFoundException();
        }
    }

    /**
     * Replaces the contents of this list with {@code assignments}.
     */
    public void setAssignments(List<Assignment> assignments) {
        requireAllNonNull(assignments);
        if (!assignmentsAreUnique(assignments)) {
            throw new DuplicateAssignmentException();
        }

        internalList.setAll(assignments);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Assignment> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Assignment> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueAssignmentList)) {
            return false;
        }

        UniqueAssignmentList otherUniqueAssignmentList = (UniqueAssignmentList) other;
        return internalList.equals(otherUniqueAssignmentList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean assignmentsAreUnique(List<Assignment> assignments) {
        for (int i = 0; i < assignments.size() - 1; i++) {
            for (int j = i + 1; j < assignments.size(); j++) {
                if (assignments.get(i).isSameAssignment(assignments.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
