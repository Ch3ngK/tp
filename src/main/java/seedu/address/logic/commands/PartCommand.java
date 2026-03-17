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
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Session;

/**
 * Assigns a participation value to a person identified using the displayed index.
 */
public class PartCommand extends Command {

    public static final String COMMAND_WORD = "part";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns participation to the person identified by the index number in the displayed person list.\n"
            + "Parameters: i/INDEX PARTICIPATION [d/YYYY-MM-DD] [g/CLASSSPACE]\n"
            + "Participation must be an integer from 0 to 5.\n"
            + "Example: " + COMMAND_WORD + " i/1 4 d/2026-03-16 g/T02";

    public static final String MESSAGE_PARTICIPATION_SUCCESS =
            "Updated participation for Person: %1$s";
    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/CLASS_SPACE.";
    public static final String MESSAGE_NO_ACTIVE_SESSION_DATE =
            "No active session date selected. Run attview d/DATE g/CLASS_SPACE first or provide d/DATE.";
    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This class space does not exist.";

    private final Index targetIndex;
    private final Participation participation;
    private final Optional<LocalDate> date;
    private final Optional<ClassSpaceName> classSpaceName;

    /**
     * Creates a PartCommand to assign the specified {@code Participation}
     * value to the person identified by the given {@code Index}.
     *
     * @param targetIndex Index of the person in the filtered person list whose participation
     *                    level is to be updated.
     * @param participation Participation value to assign to the specified person.
     */
    public PartCommand(Index targetIndex, Participation participation,
                       Optional<LocalDate> date, Optional<ClassSpaceName> classSpaceName) {
        requireAllNonNull(targetIndex, participation, date, classSpaceName);
        this.targetIndex = targetIndex;
        this.participation = participation;
        this.date = date;
        this.classSpaceName = classSpaceName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (classSpaceName.isPresent()) {
            ClassSpaceName targetName = classSpaceName.get();
            if (model.findClassSpaceByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }
            model.switchToClassSpaceView(targetName);
        }

        Optional<ClassSpaceName> activeClassSpace = model.getActiveClassSpaceName();
        if (activeClassSpace.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_CLASS_SPACE);
        }

        ClassSpaceName classSpace = activeClassSpace.get();
        LocalDate resolvedDate = resolveDate(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        Session currentSession = personToUpdate.getOrCreateSession(classSpace, resolvedDate);
        Session updatedSession = new Session(
                resolvedDate,
                currentSession.getAttendance(),
                participation
        );
        Person updatedPerson = personToUpdate.withUpdatedSession(classSpace, updatedSession);

        model.setPerson(personToUpdate, updatedPerson);
        return new CommandResult(
                String.format(MESSAGE_PARTICIPATION_SUCCESS, Messages.format(updatedPerson, classSpace, resolvedDate)));
    }

    private LocalDate resolveDate(Model model) throws CommandException {
        return date.or(() -> model.getAttendanceViewDate())
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_SESSION_DATE));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PartCommand)) {
            return false;
        }

        PartCommand otherPartCommand = (PartCommand) other;
        return targetIndex.equals(otherPartCommand.targetIndex)
                && participation.equals(otherPartCommand.participation)
                && date.equals(otherPartCommand.date)
                && classSpaceName.equals(otherPartCommand.classSpaceName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("participation", participation)
                .add("date", date)
                .add("classSpaceName", classSpaceName)
                .toString();
    }
}
