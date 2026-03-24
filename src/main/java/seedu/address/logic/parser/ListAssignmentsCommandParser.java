package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListAssignmentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListAssignmentsCommand object.
 */
public class ListAssignmentsCommandParser implements Parser<ListAssignmentsCommand> {

    @Override
    public ListAssignmentsCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListAssignmentsCommand.MESSAGE_USAGE));
        }
        return new ListAssignmentsCommand();
    }
}
