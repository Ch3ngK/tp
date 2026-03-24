package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILE;

import seedu.address.logic.commands.ExportViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ExportViewCommand object.
 */
public class ExportViewCommandParser implements Parser<ExportViewCommand> {
    @Override
    public ExportViewCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_FILE);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportViewCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FILE);
        return argMultimap.getValue(PREFIX_FILE)
                .map(ExportViewCommand::new)
                .orElseGet(ExportViewCommand::new);
    }
}
