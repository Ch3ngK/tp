package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAX_MARKS;

import java.time.LocalDate;
import java.util.stream.Stream;

import seedu.address.logic.commands.CreateAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.AssignmentName;

/**
 * Parses input arguments and creates a new CreateAssignmentCommand object.
 */
public class CreateAssignmentCommandParser implements Parser<CreateAssignmentCommand> {

    @Override
    public CreateAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT, PREFIX_DATE,
                PREFIX_MAX_MARKS);

        if (!arePrefixesPresent(argMultimap, PREFIX_ASSIGNMENT, PREFIX_DATE, PREFIX_MAX_MARKS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    CreateAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ASSIGNMENT, PREFIX_DATE, PREFIX_MAX_MARKS);

        AssignmentName assignmentName = ParserUtil.parseAssignmentName(argMultimap.getValue(PREFIX_ASSIGNMENT).get());
        LocalDate dueDate = ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get());
        int maxMarks = ParserUtil.parseMaxMarks(argMultimap.getValue(PREFIX_MAX_MARKS).get());

        return new CreateAssignmentCommand(assignmentName, dueDate, maxMarks);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
