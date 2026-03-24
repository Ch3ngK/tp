package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;

/**
 * Deletes a session for a class space on a specific date across all students in that class space.
 */
public class DeleteSessionCommand extends Command {

    public static final String COMMAND_WORD = "deletesession";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a session for a class space on a specific date.\n"
            + "Warning: this removes attendance and participation records for every student in that class space.\n"
            + "Parameters: d/YYYY-MM-DD [g/GROUP_NAME]\n"
            + "Example: " + COMMAND_WORD + " d/2026-03-16 g/T01";

    public static final String MESSAGE_SUCCESS =
            "Deleted session %1$s from class space %2$s and removed its attendance and participation records.";
    public static final String MESSAGE_CONFIRMATION =
            "This will delete session %1$s from class space %2$s for every student. Run the same command with "
                    + "\"confirm\" to proceed.";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";
    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_NOT_FOUND =
            "No session on %1$s was found in class space %2$s.";

    private final LocalDate sessionDate;
    private final Optional<ClassSpaceName> classSpaceName;
    private final boolean confirmed;

    public DeleteSessionCommand(LocalDate sessionDate) {
        this(sessionDate, Optional.empty(), false);
    }

    public DeleteSessionCommand(LocalDate sessionDate, ClassSpaceName classSpaceName) {
        this(sessionDate, Optional.of(classSpaceName), false);
    }

    /**
     * Creates a delete-session command with optional class space and confirmation state.
     */
    public DeleteSessionCommand(LocalDate sessionDate, Optional<ClassSpaceName> classSpaceName, boolean confirmed) {
        requireNonNull(sessionDate);
        requireNonNull(classSpaceName);
        this.sessionDate = sessionDate;
        this.classSpaceName = classSpaceName;
        this.confirmed = confirmed;
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

        ClassSpaceName targetClassSpace = model.getActiveClassSpaceName()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_CLASS_SPACE));

        if (!confirmed) {
            return new CommandResult(String.format(MESSAGE_CONFIRMATION, sessionDate, targetClassSpace));
        }

        SessionCommandHistory.record(model, COMMAND_WORD + " d/" + sessionDate);

        int removedCount = 0;
        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(targetClassSpace)) {
                continue;
            }
            Person updatedPerson = person.withoutSession(targetClassSpace, sessionDate);
            if (!updatedPerson.equals(person)) {
                model.setPerson(person, updatedPerson);
                removedCount++;
            }
        }

        if (removedCount == 0) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, sessionDate, targetClassSpace));
        }

        if (model.getActiveSessionDate().filter(sessionDate::equals).isPresent()) {
            model.clearActiveSessionDate();
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionDate, targetClassSpace));
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
                && classSpaceName.equals(otherCommand.classSpaceName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionDate", sessionDate)
                .add("classSpaceName", classSpaceName)
                .toString();
    }
}
