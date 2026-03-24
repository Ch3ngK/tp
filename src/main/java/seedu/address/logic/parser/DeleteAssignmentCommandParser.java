package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;

import seedu.address.logic.commands.DeleteAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.AssignmentName;

/**
 * Parses input arguments and creates a new DeleteAssignmentCommand object.
 */
public class DeleteAssignmentCommandParser implements Parser<DeleteAssignmentCommand> {

    @Override
    public DeleteAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT);
        if (!argMultimap.getValue(PREFIX_ASSIGNMENT).isPresent() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ASSIGNMENT);
        AssignmentName assignmentName = ParserUtil.parseAssignmentName(argMultimap.getValue(PREFIX_ASSIGNMENT).get());
        return new DeleteAssignmentCommand(assignmentName);
    }
}
