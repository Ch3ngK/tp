package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PartCommand;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Participation;

public class PartCommandParserTest {

    private final PartCommandParser parser = new PartCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, " i/1 d/2026-03-16 g/T01 pv/4",
                new PartCommand(Index.fromOneBased(1), Optional.of(LocalDate.of(2026, 3, 16)),
                        Optional.of(new GroupName("T01")), new Participation(4)));
    }

    @Test
    public void parse_onlyRequiredFields_success() {
        assertParseSuccess(parser, " i/1 pv/3",
                new PartCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty(), new Participation(3)));
    }

    @Test
    public void parse_missingParticipation_failure() {
        assertParseSuccess(parser, " i/1",
                new PartCommand(Optional.of(Index.fromOneBased(1)), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseSuccess(parser, " pv/3",
                new PartCommand(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new Participation(3))));
    }

    @Test
    public void parse_groupOnly_success() {
        assertParseSuccess(parser, " g/T01",
                new PartCommand(Optional.empty(), Optional.empty(), Optional.of(new GroupName("T01")), Optional.empty()));
    }

    @Test
    public void parse_noRecognizedPrefixes_failure() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
    }
}
