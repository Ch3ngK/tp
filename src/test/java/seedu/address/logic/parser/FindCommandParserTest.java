package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesFieldsPredicate;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        // whitespace only
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noPrefixes_throwsParseException() {
        // no prefixes
        assertParseFailure(parser, "Alice Bob",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validSinglePrefix_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesFieldsPredicate(
                        Arrays.asList("Alice", "Bob"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()));

        // multiple prefixes of same type
        assertParseSuccess(parser, " n/Alice n/Bob ", expectedFindCommand);
    }

    @Test
    public void parse_validMultiplePrefixes_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesFieldsPredicate(
                        Collections.singletonList("Alex"),
                        Collections.singletonList("87438807"),
                        Collections.singletonList("alexyeoh@example.com"),
                        Collections.singletonList("A1234567X"),
                        Arrays.asList("friends", "T01")));

        // multiple prefixes of different types
        assertParseSuccess(parser,
                " n/Alex p/87438807 e/alexyeoh@example.com m/A1234567X t/friends t/T01 ",
                expectedFindCommand);
    }
}
