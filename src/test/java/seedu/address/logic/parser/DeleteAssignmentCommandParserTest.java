package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteAssignmentCommand;
import seedu.address.model.assignment.AssignmentName;

public class DeleteAssignmentCommandParserTest {

    private final DeleteAssignmentCommandParser parser = new DeleteAssignmentCommandParser();

    @Test
    public void parse_validArgs_success() {
        assertParseSuccess(parser, " a/Quiz 1", new DeleteAssignmentCommand(new AssignmentName("Quiz 1")));
    }

    @Test
    public void parse_missingPrefix_failure() {
        assertParseFailure(parser, " Quiz 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAssignmentCommand.MESSAGE_USAGE));
    }
}
