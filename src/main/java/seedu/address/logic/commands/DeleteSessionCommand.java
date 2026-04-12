package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Deletes a session for a group on a specific date across all students in that group.
 */
public class DeleteSessionCommand extends Command {

    public static final String COMMAND_WORD = "deletesession";
    public static final String COMMAND_PARAMETERS = "d/YYYY-MM-DD [g/GROUP_NAME]";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a session for a group on a specific date.\n"
            + "Warning: this removes attendance and participation records for every student in that group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " d/2026-03-16 g/T01";

    public static final String MESSAGE_SUCCESS =
            "Deleted session %1$s from group %2$s and removed its attendance and participation records.";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";
    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_NOT_FOUND =
            "No session on %1$s was found in group %2$s.";

    private final LocalDate sessionDate;
    private final Optional<GroupName> groupName;

    public DeleteSessionCommand(LocalDate sessionDate) {
        this(sessionDate, Optional.empty());
    }

    public DeleteSessionCommand(LocalDate sessionDate, GroupName groupName) {
        this(sessionDate, Optional.of(groupName));
    }

    /**
     * Creates a delete-session command with an optional group.
     */
    public DeleteSessionCommand(LocalDate sessionDate, Optional<GroupName> groupName) {
        requireNonNull(sessionDate);
        requireNonNull(groupName);
        this.sessionDate = sessionDate;
        this.groupName = groupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (groupName.isPresent()) {
            GroupName targetName = groupName.get();
            if (model.findGroupByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }
            model.switchToGroupView(targetName);
        }

        GroupName targetGroup = model.getActiveGroupName()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Group group = model.findGroupByName(targetGroup)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));

        Group updatedGroup = group.withoutSession(sessionDate);
        boolean sessionRemovedFromGroup = !updatedGroup.equals(group);
        int removedCount = 0;
        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(targetGroup)) {
                continue;
            }
            Person updatedPerson = person.withoutSession(targetGroup, sessionDate);
            if (!updatedPerson.equals(person)) {
                model.setPerson(person, updatedPerson);
                removedCount++;
            }
        }

        if (!sessionRemovedFromGroup && removedCount == 0) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, sessionDate, targetGroup));
        }

        if (sessionRemovedFromGroup) {
            model.setGroup(group, updatedGroup);
        }

        if (model.getActiveSessionDate().filter(sessionDate::equals).isPresent()) {
            model.clearActiveSessionDate();
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionDate, targetGroup));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteSessionCommand)) {
            return false;
        }

        DeleteSessionCommand otherCommand = (DeleteSessionCommand) other;
        return sessionDate.equals(otherCommand.sessionDate)
                && groupName.equals(otherCommand.groupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionDate", sessionDate)
                .add("groupName", groupName)
                .toString();
    }
}
