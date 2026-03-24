package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.model.classspace.ClassSpaceName;

public class EditSessionCommandParserTest {

    private final EditSessionCommandParser parser = new EditSessionCommandParser();

    @Test
    public void parse_validArgs_returnsEditSessionCommand() {
        assertParseSuccess(parser, " d/2026-03-16 nd/2026-03-23",
                new EditSessionCommand(LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 23)));
        assertParseSuccess(parser, " d/2026-03-16 nd/2026-03-23 g/T01",
                new EditSessionCommand(LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 23),
                        new ClassSpaceName("T01")));
    }

    @Test
    public void parse_missingNewDate_throwsParseException() {
        assertParseFailure(parser, " d/2026-03-16",
                String.format(seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        EditSessionCommand.MESSAGE_USAGE));
    }
}
