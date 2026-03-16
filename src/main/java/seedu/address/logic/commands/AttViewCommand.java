package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

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
            + ": Shows attendance view for the current view or a specified tutorial group.\n"
            + "Parameters: [STATUS] [g/GROUP_NAME]\n"
            + "Allowed values: PRESENT, ABSENT, UNINITIALISED\n"
            + "Examples: " + COMMAND_WORD + "\n"
            + "          " + COMMAND_WORD + " PRESENT\n"
            + "          " + COMMAND_WORD + " g/T01\n"
            + "          " + COMMAND_WORD + " ABSENT g/T01";

    public static final String MESSAGE_SUCCESS = "Listed %1$d students with attendance %2$s in the current view";
    public static final String MESSAGE_VIEW_SUCCESS =
            "Showing attendance and participation for %1$d students in the current view";
    public static final String MESSAGE_NO_MATCHES =
            "No students with attendance %1$s were found in the current view";
    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This class space does not exist.";

    private final Optional<Attendance> attendance;
    private final Optional<ClassSpaceName> classSpaceName;

    /**
     * Creates an attendance view command for the current view without filtering by attendance status.
     */
    public AttViewCommand() {
        this.attendance = Optional.empty();
        this.classSpaceName = Optional.empty();
    }

    /**
     * Creates an attendance view command filtered by the specified attendance status.
     *
     * @param attendance Attendance status to filter by.
     */
    public AttViewCommand(Attendance attendance) {
        requireNonNull(attendance);
        this.attendance = Optional.of(attendance);
        this.classSpaceName = Optional.empty();
    }

    /**
     * Creates an attendance view command filtered by attendance status within the specified class space.
     *
     * @param attendance Attendance status to filter by.
     * @param classSpaceName Name of the class space to switch to before filtering.
     */
    public AttViewCommand(Attendance attendance, ClassSpaceName classSpaceName) {
        requireNonNull(attendance);
        requireNonNull(classSpaceName);
        this.attendance = Optional.of(attendance);
        this.classSpaceName = Optional.of(classSpaceName);
    }

    /**
     * Creates an attendance view command for the specified class space without attendance filtering.
     *
     * @param classSpaceName Name of the class space to switch to.
     */
    public AttViewCommand(ClassSpaceName classSpaceName) {
        requireNonNull(classSpaceName);
        this.attendance = Optional.empty();
        this.classSpaceName = Optional.of(classSpaceName);
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

        model.setAttendanceViewActive(true);
        if (attendance.isEmpty()) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(String.format(MESSAGE_VIEW_SUCCESS, model.getFilteredPersonList().size()));
        }

        Attendance targetAttendance = attendance.get();
        model.updateFilteredPersonList(person -> person.getAttendance().equals(targetAttendance));
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            return new CommandResult(String.format(MESSAGE_NO_MATCHES, targetAttendance));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, matchCount, targetAttendance));
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
                && classSpaceName.equals(otherAttViewCommand.classSpaceName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("attendance", attendance)
                .add("classSpaceName", classSpaceName)
                .toString();
    }
}
