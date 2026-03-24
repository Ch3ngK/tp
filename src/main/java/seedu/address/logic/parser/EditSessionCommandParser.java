package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_NOTE;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Parses input arguments and creates a new EditSessionCommand object.
 */
public class EditSessionCommandParser implements Parser<EditSessionCommand> {
    @Override
    public EditSessionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_DATE, PREFIX_NEW_DATE, PREFIX_NEW_NOTE, PREFIX_GROUP);
        if (!arePrefixesPresent(argMultimap, PREFIX_DATE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
        }
        if (argMultimap.getValue(PREFIX_NEW_DATE).isEmpty() && argMultimap.getValue(PREFIX_NEW_NOTE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE, PREFIX_NEW_DATE, PREFIX_NEW_NOTE, PREFIX_GROUP);
        LocalDate originalDate = ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get());
        Optional<LocalDate> newDate = Optional.empty();
        if (argMultimap.getValue(PREFIX_NEW_DATE).isPresent()) {
            newDate = Optional.of(ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_NEW_DATE).get()));
        }
        Optional<String> newNote = argMultimap.getValue(PREFIX_NEW_NOTE).map(String::trim);
        if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
            ClassSpaceName classSpaceName = ParserUtil.parseClassSpaceName(argMultimap.getValue(PREFIX_GROUP).get());
            return new EditSessionCommand(originalDate, newDate, newNote, Optional.of(classSpaceName));
        }
        return new EditSessionCommand(originalDate, newDate, newNote, Optional.empty());
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
