package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpace;

/**
 * Creates a new class space.
 */
public class CreateGroupCommand extends Command {

    public static final String COMMAND_WORD = "creategroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates a group.\n"
            + "Parameters: g/GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SUCCESS = "Created group: %1$s";
    public static final String MESSAGE_DUPLICATE_GROUP = "This group already exists.";

    private final ClassSpace classSpace;

    public CreateGroupCommand(ClassSpace classSpace) {
        this.classSpace = classSpace;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasClassSpace(classSpace)) {
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }
        model.addClassSpace(classSpace);
        return new CommandResult(String.format(MESSAGE_SUCCESS, classSpace.getClassSpaceName().value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateGroupCommand)) {
            return false;
        }
        CreateGroupCommand otherCommand = (CreateGroupCommand) other;
        return classSpace.equals(otherCommand.classSpace);
    }
}
