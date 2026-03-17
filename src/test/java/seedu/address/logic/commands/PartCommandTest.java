package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.testutil.PersonBuilder;

public class PartCommandTest {
    private static final ClassSpaceName T01 = new ClassSpaceName("T01");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);

    @Test
    public void execute_withActiveSessionContext_updatesSessionParticipation() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        Person originalPerson = withSession(
                new PersonBuilder().withName("Amy Bee").withMatricNumber("A1234567X")
                        .withEmail("amy@example.com").withPhone("91234567").withClassSpaces("T01").build(),
                T01, SESSION_DATE, "PRESENT", 1);
        model.addPerson(originalPerson);
        model.switchToClassSpaceView(T01);
        model.setAttendanceViewActive(true);
        model.setAttendanceViewDate(SESSION_DATE);

        PartCommand command = new PartCommand(Index.fromOneBased(1), new Participation(4),
                Optional.empty(), Optional.empty());

        Person updatedPerson = withSession(
                new PersonBuilder(originalPerson).build(),
                T01, SESSION_DATE, "PRESENT", 4);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.setAttendanceViewDate(SESSION_DATE);
        expectedModel.setPerson(originalPerson, updatedPerson);

        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                seedu.address.logic.Messages.format(updatedPerson, T01, SESSION_DATE));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(new Participation(4), model.getFilteredPersonList().get(0).getParticipation(T01, SESSION_DATE));
    }

    @Test
    public void execute_withoutDateAndNoActiveSession_throwsCommandException() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        model.addPerson(new PersonBuilder().withName("Amy Bee").withMatricNumber("A1234567X")
                .withEmail("amy@example.com").withPhone("91234567").withClassSpaces("T01").build());
        model.switchToClassSpaceView(T01);

        PartCommand command = new PartCommand(Index.fromOneBased(1), new Participation(4),
                Optional.empty(), Optional.empty());

        assertThrows(CommandException.class, PartCommand.MESSAGE_NO_ACTIVE_SESSION_DATE, () -> command.execute(model));
    }

    private Person withSession(Person person, ClassSpaceName classSpaceName, LocalDate date,
                               String attendance, int participation) {
        return person.withUpdatedSession(classSpaceName,
                new Session(date, new Attendance(attendance), new Participation(participation)));
    }
}
