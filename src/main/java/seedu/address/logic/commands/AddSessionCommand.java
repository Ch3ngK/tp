package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Adds a session for a class space on a specific date across all students in that class space.
 */
public class AddSessionCommand extends Command {

    public static final String COMMAND_WORD = "addsession";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a session for a class space on a specific date.\n"
            + "Parameters: d/YYYY-MM-DD [g/GROUP_NAME] [n/NOTE]\n"
            + "Example: " + COMMAND_WORD + " d/2026-03-16 g/T01 n/tutorial";

    public static final String MESSAGE_SUCCESS =
            "Added session %1$s to class space %2$s for %3$d students.";
    public static final String MESSAGE_SUCCESS_PARTIAL =
            "Added session %1$s to class space %2$s for %3$d students. It already existed for %4$d students.";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";
    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_ALREADY_EXISTS =
            "Session %1$s already exists for all students in class space %2$s.";

    private final LocalDate sessionDate;
    private final Optional<ClassSpaceName> classSpaceName;
    private final String note;

    public AddSessionCommand(LocalDate sessionDate) {
        this(sessionDate, Optional.empty(), "");
    }

    public AddSessionCommand(LocalDate sessionDate, ClassSpaceName classSpaceName) {
        this(sessionDate, Optional.of(classSpaceName), "");
    }

    public AddSessionCommand(LocalDate sessionDate, ClassSpaceName classSpaceName, String note) {
        this(sessionDate, Optional.of(classSpaceName), note);
    }

    public AddSessionCommand(LocalDate sessionDate, String note) {
        this(sessionDate, Optional.empty(), note);
    }

    private AddSessionCommand(LocalDate sessionDate, Optional<ClassSpaceName> classSpaceName, String note) {
        requireNonNull(sessionDate);
        requireNonNull(classSpaceName);
        requireNonNull(note);
        this.sessionDate = sessionDate;
        this.classSpaceName = classSpaceName;
        this.note = note.trim();
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
        String commandDescription = COMMAND_WORD + " d/" + sessionDate
                + (note.isBlank() ? "" : " n/" + note);
        SessionCommandHistory.record(model, commandDescription);

        int createdCount = 0;
        int existingCount = 0;
        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(targetClassSpace)) {
                continue;
            }
            boolean sessionExists = Optional.ofNullable(person.getClassSpaceSessions().get(targetClassSpace))
                    .flatMap(sessionList -> sessionList.getSession(sessionDate))
                    .isPresent();
            if (sessionExists) {
                existingCount++;
                continue;
            }

            Session defaultSession = new Session(sessionDate,
                    new Attendance(Attendance.Status.UNINITIALISED), new Participation(0), note);
            model.setPerson(person, person.withUpdatedSession(targetClassSpace, defaultSession));
            createdCount++;
        }

        if (createdCount == 0) {
            throw new CommandException(String.format(MESSAGE_SESSION_ALREADY_EXISTS, sessionDate, targetClassSpace));
        }

        model.setActiveSessionDate(sessionDate);
        if (existingCount > 0) {
            return new CommandResult(String.format(
                    MESSAGE_SUCCESS_PARTIAL, sessionDate, targetClassSpace, createdCount, existingCount));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionDate, targetClassSpace, createdCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddSessionCommand)) {
            return false;
        }

        AddSessionCommand otherCommand = (AddSessionCommand) other;
        return sessionDate.equals(otherCommand.sessionDate)
                && classSpaceName.equals(otherCommand.classSpaceName)
                && note.equals(otherCommand.note);
    }
}
