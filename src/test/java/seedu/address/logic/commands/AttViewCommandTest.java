package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

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

/**
 * Contains integration tests (interaction with the Model) for {@code AttViewCommand}.
 */
public class AttViewCommandTest {
    private static final ClassSpaceName T01 = new ClassSpaceName("T01");
    private static final ClassSpaceName T02 = new ClassSpaceName("T02");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);

    @Test
    public void equals() {
        AttViewCommand presentCommand = new AttViewCommand(new Attendance("PRESENT"), T01, SESSION_DATE);
        AttViewCommand absentCommand = new AttViewCommand(new Attendance("ABSENT"), T01, SESSION_DATE);
        AttViewCommand groupCommand = new AttViewCommand(T01, SESSION_DATE);

        assertTrue(presentCommand.equals(presentCommand));
        assertTrue(presentCommand.equals(new AttViewCommand(new Attendance("PRESENT"), T01, SESSION_DATE)));
        assertTrue(groupCommand.equals(new AttViewCommand(T01, SESSION_DATE)));
        assertFalse(presentCommand.equals(1));
        assertFalse(presentCommand.equals(null));
        assertFalse(presentCommand.equals(absentCommand));
    }

    @Test
    public void execute_presentFilter_showsMatchingPersons() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));

        model.addPerson(withSession(
                new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                        .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build(),
                T01, SESSION_DATE, "PRESENT", 3));
        model.addPerson(withSession(
                new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                        .withEmail("bob@example.com").withPhone("92345678").withClassSpaces("T01").build(),
                T01, SESSION_DATE, "ABSENT", 1));
        model.addPerson(withSession(
                new PersonBuilder().withName("Cara Present").withMatricNumber("A1234569U")
                        .withEmail("cara@example.com").withPhone("93456789").withClassSpaces("T01").build(),
                T01, SESSION_DATE, "PRESENT", 5));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Attendance attendance = new Attendance(Attendance.Status.PRESENT);
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.setAttendanceViewDate(SESSION_DATE);
        expectedModel.updateFilteredPersonList(person -> person.getAttendance(T01, SESSION_DATE).equals(attendance));

        AttViewCommand command = new AttViewCommand(attendance, T01, SESSION_DATE);
        String expectedMessage = String.format(AttViewCommand.MESSAGE_SUCCESS, 2, attendance, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_noFilter_showsCurrentGroupSessionView() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678").withClassSpaces("T01").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.setAttendanceViewDate(SESSION_DATE);

        AttViewCommand command = new AttViewCommand(T01, SESSION_DATE);
        String expectedMessage = String.format(AttViewCommand.MESSAGE_VIEW_SUCCESS, 2, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_groupView_showsWholeGroup() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        model.addClassSpace(new ClassSpace(T02));
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678").withClassSpaces("T01").build());
        model.addPerson(new PersonBuilder().withName("Cara Elsewhere").withMatricNumber("A1234569U")
                .withEmail("cara@example.com").withPhone("93456789").withClassSpaces("T02").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.setAttendanceViewDate(SESSION_DATE);

        AttViewCommand command = new AttViewCommand(T01, SESSION_DATE);
        String expectedMessage = String.format(AttViewCommand.MESSAGE_VIEW_SUCCESS, 2, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_missingGroup_throwsCommandException() {
        Model model = new ModelManager();
        AttViewCommand command = new AttViewCommand(new ClassSpaceName("Missing"), SESSION_DATE);
        assertThrows(CommandException.class, AttViewCommand.MESSAGE_GROUP_NOT_FOUND, () -> command.execute(model));
    }

    @Test
    public void execute_noMatches_returnsNoMatchesMessage() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        model.addPerson(withSession(
                new PersonBuilder().withName("Only Present").withMatricNumber("A1234567X")
                        .withEmail("present@example.com").withPhone("94567890").withClassSpaces("T01").build(),
                T01, SESSION_DATE, "PRESENT", 2));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Attendance attendance = new Attendance(Attendance.Status.ABSENT);
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.setAttendanceViewDate(SESSION_DATE);
        expectedModel.updateFilteredPersonList(person -> person.getAttendance(T01, SESSION_DATE).equals(attendance));

        AttViewCommand command = new AttViewCommand(attendance, T01, SESSION_DATE);
        String expectedMessage = String.format(AttViewCommand.MESSAGE_NO_MATCHES, attendance, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        Attendance attendance = new Attendance("ABSENT");
        AttViewCommand command = new AttViewCommand(attendance, T01, SESSION_DATE);
        String expected = AttViewCommand.class.getCanonicalName()
                + "{attendance=Optional[" + attendance + "], classSpaceName=" + T01 + ", date=" + SESSION_DATE + "}";
        assertEquals(expected, command.toString());
    }

    private Person withSession(Person person, ClassSpaceName classSpaceName, LocalDate date,
                               String attendance, int participation) {
        return person.withUpdatedSession(classSpaceName,
                new Session(date, new Attendance(attendance), new Participation(participation)));
    }
}
