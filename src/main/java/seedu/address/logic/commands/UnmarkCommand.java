package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Marks one or more persons as absent for a specified session date in the current or specified group.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";
    public static final String COMMAND_PARAMETERS = "i/INDEX_EXPRESSION d/YYYY-MM-DD";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the persons identified by the index expression in the displayed person list as ABSENT.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " i/1 d/2026-03-14\n"
            + COMMAND_WORD + " i/1,2,3 d/2026-03-14\n"
            + COMMAND_WORD + " i/1-3 d/2026-03-14\n"
            + COMMAND_WORD + " i/1 (after 'view d/2026-03-14')";

    public static final String MESSAGE_UNMARK_SUCCESS =
            "Marked Person as ABSENT: %1$s";
    public static final String MESSAGE_MULTIPLE_UNMARK_SUCCESS =
            "Marked Persons as ABSENT: %1$s";

    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This group does not exist.";

    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_REQUIRES_GROUP_VIEW =
            "Unmark attendance from a group view only. Use switchgroup g/GROUP_NAME first.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or run view with d/YYYY-MM-DD first.";

    private final List<Index> targetIndexes;
    private final Optional<LocalDate> date;
    private final Optional<GroupName> groupName;

    /**
     * Creates an UnmarkCommand to mark the persons identified by the given {@code Index} list
     * as absent for a given session date and group.
     *
     * @param targetIndexes Indexes of the persons in the filtered person list to be marked as absent.
     * @param date Date of the session to mark attendance for.
     * @param groupName Group containing this session, if explicitly provided.
     */
    public UnmarkCommand(List<Index> targetIndexes, Optional<LocalDate> date, Optional<GroupName> groupName) {
        requireAllNonNull(targetIndexes, date, groupName);
        this.targetIndexes = new ArrayList<>(targetIndexes);
        this.date = date;
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

        Optional<GroupName> activeGroup = model.getActiveGroupName();

        if (activeGroup.isEmpty()) {
            throw new CommandException(MESSAGE_REQUIRES_GROUP_VIEW);
        }

        GroupName group = activeGroup.get();
        Optional<LocalDate> resolvedDate = date.isPresent() ? date : model.getActiveSessionDate();
        if (resolvedDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }
        LocalDate targetDate = resolvedDate.get();

        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> personsToUpdate = new ArrayList<>();

        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personsToUpdate.add(lastShownList.get(targetIndex.getZeroBased()));
        }

        List<String> updatedPersons = new ArrayList<>();
        for (Person personToUpdate : personsToUpdate) {
            Session currentSession = personToUpdate.getOrCreateSession(group, targetDate);

            Session updatedSession = new Session(
                    targetDate,
                    new Attendance(Attendance.Status.ABSENT),
                    currentSession.getParticipation(),
                    currentSession.getNote()
            );

            Person updatedPerson = personToUpdate.withUpdatedSession(group, updatedSession);

            model.setPerson(personToUpdate, updatedPerson);
            updatedPersons.add(Messages.format(updatedPerson, group, targetDate));
        }

        model.setActiveSessionDate(targetDate);

        String joinedPersons = updatedPersons.stream().collect(Collectors.joining("\n"));
        if (updatedPersons.size() == 1) {
            return new CommandResult(String.format(MESSAGE_UNMARK_SUCCESS, joinedPersons));
        }
        return new CommandResult(String.format(MESSAGE_MULTIPLE_UNMARK_SUCCESS, joinedPersons));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnmarkCommand otherUnmarkCommand)) {
            return false;
        }

        return targetIndexes.equals(otherUnmarkCommand.targetIndexes)
                && date.equals(otherUnmarkCommand.date)
                && groupName.equals(otherUnmarkCommand.groupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .add("date", date)
                .add("groupName", groupName)
                .toString();
    }
}
