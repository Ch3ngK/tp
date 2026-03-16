package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Parses input arguments and creates a new UnmarkCommand object.
 */
public class UnmarkCommandParser implements Parser<UnmarkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UnmarkCommand
     * and returns an UnmarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public UnmarkCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP);

        if (!arePrefixesPresent(argMultimap, PREFIX_INDEXES, PREFIX_DATE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEXES).get());
            LocalDate date = ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get());

            Optional<ClassSpaceName> classSpaceName = Optional.empty();
            if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
                classSpaceName = Optional.of(
                        ParserUtil.parseClassSpaceName(argMultimap.getValue(PREFIX_GROUP).get())
                );
            }

            return new UnmarkCommand(index, date, classSpaceName);
        } catch (ParseException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE), e);
        }
    }

    /**
     * Returns true if all the specified prefixes are present in the argument multimap.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
