package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;

import java.time.LocalDate;
import java.util.Optional;

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
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_GROUP);
        String trimmedPreamble = argMultimap.getPreamble().trim();

        String[] preambleParts = trimmedPreamble.split("\\s+");
        if (trimmedPreamble.isEmpty() || preambleParts.length != 2 || !preambleParts[0].startsWith(PREFIX_INDEXES.toString())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(preambleParts[0].substring(PREFIX_INDEXES.toString().length()));
            Participation participation = new Participation(preambleParts[1]);

            Optional<LocalDate> date = Optional.empty();
            if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
                date = Optional.of(ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get()));
            }

            Optional<ClassSpaceName> classSpaceName = Optional.empty();
            if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
                classSpaceName = Optional.of(ParserUtil.parseClassSpaceName(argMultimap.getValue(PREFIX_GROUP).get()));
            }

            return new PartCommand(index, participation, date, classSpaceName);
        } catch (IllegalArgumentException | ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE), e);
        }
    }
}
