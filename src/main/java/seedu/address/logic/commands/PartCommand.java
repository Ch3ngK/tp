package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Assigns a participation value to a person identified using the displayed index
 * for a specified session date in the current or specified group.
 */
public class PartCommand extends Command {

    public static final String COMMAND_WORD = "part";
    public static final String COMMAND_PARAMETERS = "i/INDEX pv/PARTICIPATION_VALUE d/YYYY-MM-DD"
                    + " (PARTICIPATION_VALUE must be an integer from 0 to 5)\n";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns participation to the person identified by the index number in the displayed person list.\n"
            + "Participation updates are only available in group view. Run switchgroup g/GROUP_NAME first,\n"
            + "and use view d/YYYY-MM-DD if you need to select a session before updating participation.\n"
            + "Parameters: " + COMMAND_PARAMETERS
            + "            i/INDEX pv/PARTICIPATION_VALUE (after 'view d/YYYY-MM-DD')\n"
            + "Examples:\n"
            + COMMAND_WORD + " i/1 pv/5 d/2026-03-14\n"
            + COMMAND_WORD + " i/1 pv/5 (after 'view d/2026-03-14')";

    public static final String MESSAGE_PARTICIPATION_SUCCESS =
            "Updated participation for Person: %1$s";
    public static final String MESSAGE_MISSING_REQUIRED_FIELDS =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE);

    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This group does not exist.";

    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "Update participation from a group view only. Use switchgroup g/GROUP_NAME first.";
    public static final String MESSAGE_REQUIRES_GROUP_VIEW =
            "Update participation from a group view only. Use switchgroup g/GROUP_NAME first.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or run view with d/YYYY-MM-DD first.";

    private final Optional<Index> targetIndex;
    private final Optional<LocalDate> date;
    private final Optional<GroupName> groupName;
    private final Optional<Participation> participation;

    /**
     * Creates a PartCommand to assign the specified {@code Participation}
     * value to the person identified by the given {@code Index}, for the
     * specified session date and current or specified group.
     *
     * @param targetIndex Index of the person in the filtered person list whose participation
     *                    level is to be updated.
     * @param date Date of the session to update participation for.
     * @param groupName Group containing this session, if explicitly provided.
     * @param participation Participation value to assign to the specified person.
     */
    public PartCommand(Index targetIndex, Optional<LocalDate> date, Optional<GroupName> groupName,
                       Participation participation) {
        this(Optional.of(targetIndex), date, groupName, Optional.of(participation));
    }

    /**
     * Creates a PartCommand using optional required fields so execute() can prioritize
     * group-view errors over format errors when appropriate.
     */
    public PartCommand(Optional<Index> targetIndex, Optional<LocalDate> date, Optional<GroupName> groupName,
                       Optional<Participation> participation) {
        requireAllNonNull(targetIndex, date, groupName, participation);
        this.targetIndex = targetIndex;
        this.date = date;
        this.groupName = groupName;
        this.participation = participation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getActiveGroupName().isEmpty()) {
            throw new CommandException(MESSAGE_REQUIRES_GROUP_VIEW);
        }

        if (targetIndex.isEmpty() || participation.isEmpty()) {
            throw new CommandException(MESSAGE_MISSING_REQUIRED_FIELDS);
        }

        // Step 1: switch group if g/ provided
        if (groupName.isPresent()) {
            GroupName targetName = groupName.get();

            if (model.findGroupByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }

            model.switchToGroupView(targetName);
        }

        // Step 2: resolve active group
        Optional<GroupName> activeGroup = model.getActiveGroupName();

        if (activeGroup.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_GROUP);
        }

        GroupName group = activeGroup.get();
        Optional<LocalDate> resolvedDate = date.isPresent() ? date : model.getActiveSessionDate();
        if (resolvedDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }
        LocalDate targetDate = resolvedDate.get();
        Index resolvedIndex = targetIndex.orElseThrow();
        Participation resolvedParticipation = participation.orElseThrow();
        SessionCommandHistory.record(model,
                COMMAND_WORD + " i/" + resolvedIndex.getOneBased()
                        + " d/" + targetDate + " pv/" + resolvedParticipation.value);

        // Step 3: get person
        List<Person> lastShownList = model.getFilteredPersonList();

        if (resolvedIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(resolvedIndex.getZeroBased());

        // Step 4: get session
        Session currentSession = personToUpdate.getOrCreateSession(group, targetDate);

        // Step 5: update participation
        Session updatedSession = new Session(
                targetDate,
                currentSession.getAttendance(),
                resolvedParticipation,
                currentSession.getNote()
        );

        // Step 6: update person
        Person updatedPerson = personToUpdate.withUpdatedSession(group, updatedSession);

        // Step 7: update model
        model.setPerson(personToUpdate, updatedPerson);
        model.setActiveSessionDate(targetDate);

        return new CommandResult(String.format(
                MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, group, targetDate))
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PartCommand otherPartCommand)) {
            return false;
        }

        return targetIndex.equals(otherPartCommand.targetIndex)
                && date.equals(otherPartCommand.date)
                && groupName.equals(otherPartCommand.groupName)
                && participation.equals(otherPartCommand.participation);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .add("groupName", groupName)
                .add("participation", participation)
                .toString();
    }
}
