package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPATION;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PartCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Participation;

/**
 * Parses input arguments and creates a new PartCommand object.
 */
public class PartCommandParser implements Parser<PartCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PartCommand
     * and returns a PartCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public PartCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP, PREFIX_PARTICIPATION);

        if (!arePrefixesPresent(argMultimap, PREFIX_INDEXES, PREFIX_DATE, PREFIX_PARTICIPATION)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP, PREFIX_PARTICIPATION);

        try {
            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEXES).get());
            LocalDate date = ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get());

            Optional<ClassSpaceName> classSpaceName = Optional.empty();
            if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
                classSpaceName = Optional.of(
                        ParserUtil.parseClassSpaceName(argMultimap.getValue(PREFIX_GROUP).get())
                );
            }

            Participation participation =
                    new Participation(argMultimap.getValue(PREFIX_PARTICIPATION).get());

            return new PartCommand(index, date, classSpaceName, participation);
        } catch (IllegalArgumentException | ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE), e);
        }
    }

    /**
     * Returns true if all the specified prefixes are present in the argument multimap.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}