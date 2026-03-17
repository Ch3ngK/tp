package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;

/**
 * Filters the current view to persons with the specified attendance status.
 */
public class AttViewCommand extends Command {

    public static final String COMMAND_WORD = "attview";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows attendance and participation for a tutorial group's session on a given date.\n"
            + "Parameters: [STATUS] d/YYYY-MM-DD g/GROUP_NAME\n"
            + "Allowed values: PRESENT, ABSENT, UNINITIALISED\n"
            + "Examples: " + COMMAND_WORD + " d/2026-03-16 g/T01\n"
            + "          " + COMMAND_WORD + " PRESENT d/2026-03-16 g/T01";

    public static final String MESSAGE_SUCCESS =
            "Listed %1$d students with attendance %2$s in class space %3$s on %4$s";
    public static final String MESSAGE_VIEW_SUCCESS =
            "Showing attendance and participation for %1$d students in class space %2$s on %3$s";
    public static final String MESSAGE_NO_MATCHES =
            "No students with attendance %1$s were found in class space %2$s on %3$s";
    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This class space does not exist.";

    private final Optional<Attendance> attendance;
    private final ClassSpaceName classSpaceName;
    private final LocalDate date;

    /**
     * Creates an attendance view command for the specified class space and session date without attendance filtering.
     */
    public AttViewCommand(ClassSpaceName classSpaceName, LocalDate date) {
        requireNonNull(classSpaceName);
        requireNonNull(date);
        this.attendance = Optional.empty();
        this.classSpaceName = classSpaceName;
        this.date = date;
    }

    /**
     * Creates an attendance view command filtered by attendance status within the specified class space.
     *
     * @param attendance Attendance status to filter by.
     * @param classSpaceName Name of the class space to switch to before filtering.
     * @param date Session date to display.
     */
    public AttViewCommand(Attendance attendance, ClassSpaceName classSpaceName, LocalDate date) {
        requireNonNull(attendance);
        requireNonNull(classSpaceName);
        requireNonNull(date);
        this.attendance = Optional.of(attendance);
        this.classSpaceName = classSpaceName;
        this.date = date;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.findClassSpaceByName(classSpaceName).isEmpty()) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        model.switchToClassSpaceView(classSpaceName);

        model.setAttendanceViewActive(true);
        model.setAttendanceViewDate(date);
        if (attendance.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_VIEW_SUCCESS,
                    model.getFilteredPersonList().size(), classSpaceName, date));
        }

        Attendance targetAttendance = attendance.get();
        model.updateFilteredPersonList(person -> person.getAttendance(classSpaceName, date).equals(targetAttendance));
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            return new CommandResult(String.format(MESSAGE_NO_MATCHES, targetAttendance, classSpaceName, date));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, matchCount, targetAttendance, classSpaceName, date));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AttViewCommand otherAttViewCommand)) {
            return false;
        }

        return attendance.equals(otherAttViewCommand.attendance)
                && classSpaceName.equals(otherAttViewCommand.classSpaceName)
                && date.equals(otherAttViewCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("attendance", attendance)
                .add("classSpaceName", classSpaceName)
                .add("date", date)
                .toString();
    }
}
