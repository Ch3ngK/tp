package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteSessionCommand;
import seedu.address.model.group.GroupName;

public class DeleteSessionCommandParserTest {

    private final DeleteSessionCommandParser parser = new DeleteSessionCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteSessionCommand() {
        assertParseSuccess(parser, " d/2026-03-16", new DeleteSessionCommand(LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, " d/2026-03-16 g/T01",
                new DeleteSessionCommand(LocalDate.of(2026, 3, 16), new GroupName("T01")));
    }

    @Test
    public void parse_missingDate_throwsParseException() {
        assertParseFailure(parser, " g/T01",
                String.format(seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_confirmPreamble_throwsParseException() {
        assertParseFailure(parser, " confirm d/2026-03-16",
                String.format(seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteSessionCommand.MESSAGE_USAGE));
    }
}
