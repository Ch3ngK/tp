package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Switches the current displayed view to all students or a class space.
 */
public class SwitchGroupCommand extends Command {

    public static final String COMMAND_WORD = "switchgroup";
    public static final String ALL_VIEW_KEYWORD = "all";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Switches the current displayed view.\n"
            + "Parameters: all OR g/GROUP_NAME\n"
            + "Examples: " + COMMAND_WORD + " all\n"
            + "          " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SWITCHED_TO_ALL = "Switched to all students view.";
    public static final String MESSAGE_SWITCHED_TO_GROUP = "Switched to class space: %1$s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";

    private final Optional<ClassSpaceName> classSpaceName;

    public SwitchGroupCommand() {
        this.classSpaceName = Optional.empty();
    }

    public SwitchGroupCommand(ClassSpaceName classSpaceName) {
        this.classSpaceName = Optional.of(classSpaceName);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.setAttendanceViewActive(false);
        if (classSpaceName.isEmpty()) {
            model.switchToAllStudentsView();
            return new CommandResult(MESSAGE_SWITCHED_TO_ALL);
        }

        ClassSpaceName targetName = classSpaceName.get();
        if (model.findClassSpaceByName(targetName).isEmpty()) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        model.switchToClassSpaceView(targetName);
        return new CommandResult(String.format(MESSAGE_SWITCHED_TO_GROUP, targetName.value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SwitchGroupCommand)) {
            return false;
        }
        SwitchGroupCommand otherCommand = (SwitchGroupCommand) other;
        return Objects.equals(classSpaceName, otherCommand.classSpaceName);
    }
}
