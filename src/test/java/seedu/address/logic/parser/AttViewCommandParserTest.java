package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttViewCommand;
import seedu.address.model.person.Attendance;

public class AttViewCommandParserTest {

    private final AttViewCommandParser parser = new AttViewCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseSuccess(parser, "     ", new AttViewCommand());
    }

    @Test
    public void parse_invalidArg_throwsParseException() {
        assertParseFailure(parser, "late",
                AttViewCommandParser.MESSAGE_INVALID_ATTENDANCE_STATUS + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, "present absent",
                AttViewCommandParser.MESSAGE_TOO_MANY_ARGUMENTS + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validArgs_returnsAttViewCommand() {
        assertParseSuccess(parser, "", new AttViewCommand());
        assertParseSuccess(parser, "present", new AttViewCommand(new Attendance("PRESENT")));
        assertParseSuccess(parser, "  ABSENT  ", new AttViewCommand(new Attendance("ABSENT")));
        assertParseSuccess(parser, "g/T01",
                new AttViewCommand(new seedu.address.model.classspace.ClassSpaceName("T01")));
        assertParseSuccess(parser, "present g/T01",
                new AttViewCommand(new Attendance("PRESENT"),
                        new seedu.address.model.classspace.ClassSpaceName("T01")));
    }
}
