package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.model.classspace.ClassSpaceName;

public class UnmarkCommandParserTest {
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);
    private final UnmarkCommandParser parser = new UnmarkCommandParser();

    @Test
    public void parse_indexOnly_success() {
        assertParseSuccess(parser, " i/1",
                new UnmarkCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty()));
    }

    @Test
    public void parse_withDateAndGroup_success() {
        assertParseSuccess(parser, " i/1 d/2026-03-16 g/T01",
                new UnmarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE),
                        Optional.of(new ClassSpaceName("T01"))));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " d/2026-03-16",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE));
    }
}
