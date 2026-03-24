package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExportViewCommand;

public class ExportViewCommandParserTest {

    private final ExportViewCommandParser parser = new ExportViewCommandParser();

    @Test
    public void parse_validArgs_returnsExportViewCommand() {
        assertParseSuccess(parser, "", new ExportViewCommand());
        assertParseSuccess(parser, " f/out.csv", new ExportViewCommand("out.csv"));
    }
}
