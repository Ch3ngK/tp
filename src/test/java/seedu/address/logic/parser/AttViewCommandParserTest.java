package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttViewCommand;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;

public class AttViewCommandParserTest {
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);
    private static final ClassSpaceName T01 = new ClassSpaceName("T01");

    private final AttViewCommandParser parser = new AttViewCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                AttViewCommandParser.MESSAGE_MISSING_CONTEXT + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidArg_throwsParseException() {
        assertParseFailure(parser, "late d/2026-03-16 g/T01",
                AttViewCommandParser.MESSAGE_INVALID_ATTENDANCE_STATUS + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, "present absent d/2026-03-16 g/T01",
                AttViewCommandParser.MESSAGE_TOO_MANY_ARGUMENTS + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validArgs_returnsAttViewCommand() {
        assertParseSuccess(parser, "d/2026-03-16 g/T01", new AttViewCommand(T01, SESSION_DATE));
        assertParseSuccess(parser, "present d/2026-03-16 g/T01",
                new AttViewCommand(new Attendance("PRESENT"), T01, SESSION_DATE));
        assertParseSuccess(parser, "  ABSENT  d/2026-03-16 g/T01",
                new AttViewCommand(new Attendance("ABSENT"), T01, SESSION_DATE));
    }

    @Test
    public void parse_missingContext_throwsParseException() {
        assertParseFailure(parser, "g/T01",
                AttViewCommandParser.MESSAGE_MISSING_CONTEXT + "\n" + AttViewCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "d/2026-03-16",
                AttViewCommandParser.MESSAGE_MISSING_CONTEXT + "\n" + AttViewCommand.MESSAGE_USAGE);
    }
}
