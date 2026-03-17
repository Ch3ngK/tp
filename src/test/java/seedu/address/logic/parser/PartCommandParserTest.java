package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PartCommand;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Participation;

public class PartCommandParserTest {
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);
    private final PartCommandParser parser = new PartCommandParser();

    @Test
    public void parse_indexAndParticipation_success() {
        assertParseSuccess(parser, " i/1 4",
                new PartCommand(Index.fromOneBased(1), new Participation(4),
                        Optional.empty(), Optional.empty()));
    }

    @Test
    public void parse_withDateAndGroup_success() {
        assertParseSuccess(parser, " i/1 4 d/2026-03-16 g/T01",
                new PartCommand(Index.fromOneBased(1), new Participation(4),
                        Optional.of(SESSION_DATE), Optional.of(new ClassSpaceName("T01"))));
    }

    @Test
    public void parse_missingParticipation_failure() {
        assertParseFailure(parser, " i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
    }
}
