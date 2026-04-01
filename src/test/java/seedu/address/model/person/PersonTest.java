package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.FIVE_PARTICIPATION;
import static seedu.address.logic.commands.CommandTestUtil.ONE_PARTICIPATION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_BOB_LOWERCASE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.ZERO_PARTICIPATION;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {
    private GroupName testGroup = new GroupName("T01");

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same matriculation number, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different matriculation number, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withMatricNumber(VALID_MATRIC_NUMBER_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, but matriculation number is same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        //different case for matriculation number -> returns true
        editedBob = new PersonBuilder(BOB).withMatricNumber(VALID_MATRIC_NUMBER_BOB_LOWERCASE).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void getOrCreateSession_sessionExists_returnsExistingSession() {
        LocalDate date = LocalDate.now();
        Session existingSession = new Session(date, new Attendance(Attendance.Status.PRESENT),
                new Participation(FIVE_PARTICIPATION));
        Person person = new PersonBuilder(ALICE).build().withUpdatedSession(testGroup, existingSession);

        Session retrievedSession = person.getOrCreateSession(testGroup, date);
        assertEquals(existingSession, retrievedSession);
    }

    @Test
    public void getOrCreateSession_sessionDoesNotExist_returnsDefaultSession() {
        LocalDate date = LocalDate.now();
        Person person = new PersonBuilder(ALICE).build(); // Person with no sessions.

        Session createdSession = person.getOrCreateSession(testGroup, date);

        // Check for default values
        assertEquals(date, createdSession.getDate());
        assertEquals(new Attendance(Attendance.Status.UNINITIALISED), createdSession.getAttendance());
        assertEquals(new Participation(ZERO_PARTICIPATION), createdSession.getParticipation());

        // Ensure original person object was not mutated
        assertTrue(person.getGroupSessions().isEmpty());
    }

    @Test
    public void withUpdatedSession_addNewSession_returnsNewPersonWithSession() {
        Person originalPerson = new PersonBuilder(ALICE).build();
        LocalDate date = LocalDate.now();
        Session newSession = new Session(date, new Attendance(Attendance.Status.PRESENT),
                new Participation(ONE_PARTICIPATION));

        Person updatedPerson = originalPerson.withUpdatedSession(testGroup, newSession);

        // Check that the new person has the session, and the old one does not.
        assertFalse(originalPerson.equals(updatedPerson));
        assertTrue(originalPerson.getGroupSessions().isEmpty());
        assertEquals(newSession, updatedPerson.getOrCreateSession(testGroup, date));
    }

    @Test
    public void withUpdatedSession_updateExistingSession_returnsNewPersonWithUpdatedSession() {
        LocalDate date = LocalDate.now();
        Session initialSession = new Session(date, new Attendance(Attendance.Status.UNINITIALISED),
                new Participation(ZERO_PARTICIPATION));
        Person originalPerson = new PersonBuilder(ALICE)
                .build()
                .withUpdatedSession(testGroup, initialSession);

        Session updatedSession = new Session(date, new Attendance(Attendance.Status.PRESENT),
                new Participation(FIVE_PARTICIPATION));
        Person updatedPerson = originalPerson.withUpdatedSession(testGroup, updatedSession);

        // Check that the person was updated correctly.
        assertFalse(originalPerson.equals(updatedPerson));
        assertEquals(updatedSession, updatedPerson.getOrCreateSession(testGroup, date));
        assertEquals(1, updatedPerson.getGroupSessions().get(testGroup).getSessions().size());
    }


    @Test
    public void withUpdatedAssignmentGrade_addNewGrade_returnsNewPersonWithGrade() {
        Person originalPerson = new PersonBuilder(ALICE).withGroups("T01").build();
        AssignmentName assignmentName = new AssignmentName("Quiz 1");

        Person updatedPerson = originalPerson.withUpdatedAssignmentGrade(testGroup, assignmentName, 17);

        assertTrue(originalPerson.getAssignmentGrade(testGroup, assignmentName).isEmpty());
        assertEquals(17, updatedPerson.getAssignmentGrade(testGroup, assignmentName).orElseThrow());
    }

    @Test
    public void withoutGroupData_removesSessionsAndAssignmentGrades() {
        AssignmentName assignmentName = new AssignmentName("Quiz 1");
        Person originalPerson = new PersonBuilder(ALICE).withGroups("T01")
                .withSession("T01", LocalDate.now().toString(), "PRESENT", ONE_PARTICIPATION)
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build();

        Person updatedPerson = originalPerson.withoutGroupData(testGroup);

        assertFalse(updatedPerson.hasGroup(testGroup));
        assertTrue(updatedPerson.getGroupSessions().isEmpty());
        assertTrue(updatedPerson.getAssignmentGrade(testGroup, assignmentName).isEmpty());
    }

    @Test
    public void withRenamedGroup_renamesSessionsAndAssignmentGrades() {
        AssignmentName assignmentName = new AssignmentName("Quiz 1");
        GroupName renamedGroup = new GroupName("T02");
        Person originalPerson = new PersonBuilder(ALICE).withGroups("T01")
                .withSession("T01", LocalDate.now().toString(), "PRESENT", ONE_PARTICIPATION)
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build();

        Person updatedPerson = originalPerson.withRenamedGroup(testGroup, renamedGroup);

        assertFalse(updatedPerson.hasGroup(testGroup));
        assertTrue(updatedPerson.hasGroup(renamedGroup));
        assertTrue(updatedPerson.getGroupSessions().containsKey(renamedGroup));
        assertEquals(18, updatedPerson.getAssignmentGrade(renamedGroup, assignmentName).orElseThrow());
    }

    @Test
    public void equals_differentSessionMaps_returnsFalse() {
        // Two persons, one with a session and one without.
        Person personWithSession = new PersonBuilder(ALICE).build()
                .withUpdatedSession(testGroup, new Session(LocalDate.now(),
                        new Attendance(Attendance.Status.PRESENT), new Participation(ONE_PARTICIPATION)));
        Person personWithoutSession = new PersonBuilder(ALICE).build();

        assertFalse(personWithSession.equals(personWithoutSession));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different matriculation number -> returns false
        editedAlice = new PersonBuilder(ALICE).withMatricNumber(VALID_MATRIC_NUMBER_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", matricNumber=" + ALICE.getMatricNumber() + ", tags="
                + ALICE.getTags() + ", groups=" + ALICE.getGroups() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void hashCode_test() {
        //same person, same hashcode.
        assertEquals(ALICE.hashCode(), ALICE.hashCode());

        //different person, different hashcode.
        assertNotEquals(ALICE.hashCode(), BOB.hashCode());
    }

    @Test
    public void getNameValue_returnsFullNameString() {
        assertEquals(ALICE.getName().fullName, ALICE.getNameValue());
    }

    @Test
    public void getPhoneValue_returnsPhoneString() {
        assertEquals(ALICE.getPhone().value, ALICE.getPhoneValue());
    }

    @Test
    public void getEmailValue_returnsEmailString() {
        assertEquals(ALICE.getEmail().value, ALICE.getEmailValue());
    }

    @Test
    public void getMatricNumberValue_returnsMatricNumberString() {
        assertEquals(ALICE.getMatricNumber().value, ALICE.getMatricNumberValue());
    }
}
