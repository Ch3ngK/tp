package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.time.LocalDate;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Parses input arguments and creates a new AddSessionCommand object.
 */
public class AddSessionCommandParser implements Parser<AddSessionCommand> {

    @Override
    public AddSessionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_GROUP, PREFIX_NOTE);
        if (!arePrefixesPresent(argMultimap, PREFIX_DATE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE, PREFIX_GROUP, PREFIX_NOTE);
        LocalDate sessionDate = ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get());
        String note = argMultimap.getValue(PREFIX_NOTE).map(String::trim).orElse("");
        if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
            ClassSpaceName classSpaceName = ParserUtil.parseClassSpaceName(argMultimap.getValue(PREFIX_GROUP).get());
            return new AddSessionCommand(sessionDate, classSpaceName, note);
        }
        return new AddSessionCommand(sessionDate, note);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
