package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.PersonBuilder;

public class ExportViewCommandTest {
    private static final GroupName T01 = new GroupName("T01");

    @Test
    public void execute_exportsCsv() throws Exception {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", LocalDate.of(2026, 3, 16).toString(), "PRESENT", 1).build());

        Path output = Path.of("build", "tmp", "export-view-test.csv");
        ExportViewCommand command = new ExportViewCommand(output.toString());
        command.execute(model);

        assertTrue(Files.exists(output));
        assertTrue(Files.readString(output).contains("Student"));
        assertTrue(Files.readString(output).contains("Alice"));
    }

    @Test
    public void execute_withoutActiveGroup_throwsCommandException() {
        Model model = new ModelManager();
        ExportViewCommand command = new ExportViewCommand();
        assertThrows(CommandException.class, ExportViewCommand.MESSAGE_NO_ACTIVE_GROUP, () -> {
            command.execute(model);
        });
    }

    @Test
    public void execute_withIllegalFileName_throwsCommandException() {
        Model model = getModelWithActiveGroup();
        ExportViewCommand command = new ExportViewCommand("bad\u0000name.csv");

        assertThrows(CommandException.class, String.format(
                ExportViewCommand.MESSAGE_INVALID_FILE_NAME, "bad\\0name.csv", "\\0"), () -> {
            command.execute(model);
        });
    }

    @Test
    public void execute_withReservedFileName_throwsCommandException() {
        Model model = getModelWithActiveGroup();
        ExportViewCommand command = new ExportViewCommand("CON.csv");

        assertThrows(CommandException.class, String.format(
                ExportViewCommand.MESSAGE_INVALID_FILE_NAME_RESERVED, "CON.csv", "CON"), () -> {
            command.execute(model);
        });
    }

    @Test
    public void execute_withForwardSlashRootedPath_throwsCommandException() {
        Model model = getModelWithActiveGroup();
        ExportViewCommand command = new ExportViewCommand("/T02");

        assertThrows(CommandException.class, String.format(
                ExportViewCommand.MESSAGE_INVALID_FILE_NAME, "/T02", "'/'"), () -> {
            command.execute(model);
        });
    }

    @Test
    public void execute_withBackslashRootedPath_throwsCommandException() {
        Model model = getModelWithActiveGroup();
        ExportViewCommand command = new ExportViewCommand("\\T02");

        assertThrows(CommandException.class, String.format(
                ExportViewCommand.MESSAGE_INVALID_FILE_NAME, "\\T02", "'\\'"), () -> {
            command.execute(model);
        });
    }

    private Model getModelWithActiveGroup() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        return model;
    }
}
