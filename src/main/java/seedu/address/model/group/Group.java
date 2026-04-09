package seedu.address.model.group;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.UniqueAssignmentList;
import seedu.address.model.person.Session;
import seedu.address.model.person.SessionList;

/**
 * Group represents a real-world Group, such as Tutorial Group, Lab Group, and similar.
 * This concept is purely internal to the codebase, and all user-facing strings should use the term
 * "{@code GROUP_NAME}".
 */
// @@author ongrussell
public class Group {
    private final GroupName groupName;
    private final UniqueAssignmentList assignments;
    private final SessionList sessions;

    /**
     * Creates a {@code Group} with the given name.
     *
     * @param groupName Name of the group.
     */
    public Group(GroupName groupName) {
        this(groupName, List.of(), List.of());
    }

    /**
     * Creates a {@code Group} with the given name and assignments.
     */
    public Group(GroupName groupName, List<Assignment> assignments) {
        this(groupName, assignments, List.of());
    }

    /**
     * Creates a {@code Group} with the given name, assignments, and sessions.
     */
    public Group(GroupName groupName, List<Assignment> assignments, List<Session> sessions) {
        requireNonNull(groupName);
        requireNonNull(assignments);
        requireNonNull(sessions);
        this.groupName = groupName;
        this.assignments = new UniqueAssignmentList();
        this.assignments.setAssignments(assignments);
        this.sessions = new SessionList(sessions);
    }

    public GroupName getGroupName() {
        return groupName;
    }

    /**
     * Returns an unmodifiable view of the assignments belonging to this group.
     */
    public ObservableList<Assignment> getAssignments() {
        return assignments.asUnmodifiableObservableList();
    }

    /**
     * Returns an unmodifiable view of the sessions belonging to this group.
     */
    public List<Session> getSessions() {
        return sessions.getSessions();
    }

    /**
     * Returns the session with the given date if present.
     */
    public Optional<Session> getSession(java.time.LocalDate date) {
        requireNonNull(date);
        return sessions.getSession(date);
    }

    /**
     * Returns a copy of this group with the given assignments.
     */
    public Group withAssignments(List<Assignment> updatedAssignments) {
        requireNonNull(updatedAssignments);
        return new Group(groupName, updatedAssignments, sessions.getSessions());
    }

    /**
     * Returns a copy of this group with the given session added or overwritten.
     */
    public Group withUpdatedSession(Session session) {
        requireNonNull(session);
        SessionList updatedSessions = new SessionList(sessions.getSessions());
        updatedSessions.addSession(session);
        return new Group(
                groupName,
                List.copyOf(assignments.asUnmodifiableObservableList()),
                updatedSessions.getSessions());
    }

    /**
     * Returns a copy of this group without the session for the given date.
     */
    public Group withoutSession(java.time.LocalDate date) {
        requireNonNull(date);
        SessionList updatedSessions = new SessionList(sessions.getSessions());
        if (!updatedSessions.removeSession(date)) {
            return this;
        }
        return new Group(
                groupName,
                List.copyOf(assignments.asUnmodifiableObservableList()),
                updatedSessions.getSessions());
    }

    /**
     * Returns a copy of this group with a different name.
     */
    public Group withName(GroupName newGroupName) {
        requireNonNull(newGroupName);
        return new Group(
                newGroupName,
                List.copyOf(assignments.asUnmodifiableObservableList()),
                sessions.getSessions());
    }

    /**
     * Returns whether this group contains an assignment with the given name.
     */
    public boolean hasAssignment(AssignmentName assignmentName) {
        requireNonNull(assignmentName);
        return findAssignmentByName(assignmentName).isPresent();
    }

    /**
     * Returns the assignment with the given name if present.
     */
    public Optional<Assignment> findAssignmentByName(AssignmentName assignmentName) {
        requireNonNull(assignmentName);
        return assignments.findAssignmentByName(assignmentName);
    }

    /**
     * Returns true if both groups have the same identity.
     */
    public boolean isSameGroup(Group otherGroup) {
        if (otherGroup == this) {
            return true;
        }

        return otherGroup != null
                && groupName.equals(otherGroup.groupName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Group)) {
            return false;
        }

        Group otherGroup = (Group) other;
        return groupName.equals(otherGroup.groupName)
                && assignments.equals(otherGroup.assignments)
                && sessions.equals(otherGroup.sessions);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(groupName, assignments, sessions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("groupName", groupName)
                .add("assignments", assignments)
                .add("sessions", sessions)
                .toString();
    }

    /**
     * Returns name in GroupName as a String.
     *
     * @return GroupName as a String.
     */
    public String getGroupNameValue() {
        return groupName.value;
    }
}
// @@author
