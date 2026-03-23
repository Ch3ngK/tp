package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.testutil.TypicalPersons;


public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path PERSON_WITH_MULTIPLE_INVALID_FIELDS =
            TEST_DATA_FOLDER.resolve("invalidPersonAddressBookWithMultipleInvalidFields.json");
    private static final Path IMPLICIT_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("personWithImplicitClassSpaceAddressBook.json");
    private static final Path MISSING_NAME_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("missingNamePersonAddressBook.json");
    private static final Path JSON_NULL_NAME_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("jsonNullNamePersonAddressBook.json");
    private static final Path DUPLICATE_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("duplicateClassSpaceAddressBook.json");
    private static final Path INVALID_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("invalidClassSpaceAddressBook.json");

    @Test
    public void toModelType_invalidPersonWithMultipleInvalidFields_formatsWarningAsBulletList() throws Exception {

        JsonSerializableAddressBook dataFromFile =
                JsonUtil.readJsonFile(PERSON_WITH_MULTIPLE_INVALID_FIELDS, JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        String expectedWarning = "Skipped invalid contact 'Hans Must!er':\n"
                + "- " + Name.MESSAGE_CONSTRAINTS + "\n"
                + "- " + Email.getDiagnosticMessage("hans@example.com.d") + "\n"
                + "- " + String.format(MatricNumber.MESSAGE_INVALID_CHECKSUM, 'X');

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertEquals(expectedWarning, dataFromFile.getLoadWarnings().get(0));
    }

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_skipsInvalidPerson() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Since the file only contains 1 invalid person, it should skip it and return 0 persons.
        assertEquals(0, addressBookFromFile.getPersonList().size());
    }

    @Test
    public void toModelType_duplicatePersons_skipsDuplicatePerson() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Since the file contains 2 duplicates, it skips the 2nd one and loads exactly 1 person.
        assertEquals(1, addressBookFromFile.getPersonList().size());
    }

    @Test
    public void toModelType_invalidPersonFile_preservesSkippedRawPersonAndWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
    }

    @Test
    public void toModelType_duplicatePersons_preservesSkippedDuplicateAndWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped duplicate contact"));
    }

    @Test
    public void constructor_nullPreservedSkippedPersons_success() throws Exception {
        AddressBook addressBook = TypicalPersons.getTypicalAddressBook();

        // Create the serializable book with a null list for skipped persons
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(addressBook, null);

        // Verify it doesn't crash and still correctly models the valid persons
        assertEquals(addressBook.getPersonList().size(), serializable.toModelType().getPersonList().size());
        assertEquals(0, serializable.getPreservedSkippedPersons().size());
    }

    @Test
    public void toModelType_personWithImplicitClassSpace_createsClassSpaceAutomatically() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(IMPLICIT_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Verify the person was loaded successfully.
        assertEquals(1, addressBookFromFile.getPersonList().size());

        // Verify that the app automatically created the missing class space.
        assertEquals(1, addressBookFromFile.getClassSpaceList().size());

        // Verify that it is the expected class space.
        seedu.address.model.classspace.ClassSpace expectedClassSpace =
                new seedu.address.model.classspace.ClassSpace(
                        new seedu.address.model.classspace.ClassSpaceName("Implicit-Class-Space"));

        assertTrue(addressBookFromFile.hasClassSpace(expectedClassSpace));
    }

    @Test
    public void constructor_readOnlyAddressBook_convertsCorrectly() {
        AddressBook typicalAddressBook = TypicalPersons.getTypicalAddressBook();
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(typicalAddressBook);

        // Should not throw exceptions, just to check equality.
        try {
            assertEquals(typicalAddressBook, serializable.toModelType());
        } catch (Exception e) {
            throw new AssertionError("Conversion should not fail.", e);
        }
    }

    @Test
    public void toModelType_missingName_generatesCorrectWarning() throws Exception {
        assertMissingNameWarningIsGenerated(MISSING_NAME_PERSON_FILE);
    }

    @Test
    public void toModelType_jsonNullName_generatesCorrectWarning() throws Exception {
        assertMissingNameWarningIsGenerated(JSON_NULL_NAME_PERSON_FILE);
    }

    @Test
    public void toModelType_duplicateClassSpaces_skipsDuplicateClassSpaceAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getClassSpaceList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped duplicate class space"));
    }

    @Test
    public void constructor_nullLists_doesNotThrow() throws Exception {
        // Force the "if (persons != null)" to evaluate to false
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(
                (List<JsonNode>) null, (List<JsonNode>) null);

        AddressBook addressBook = serializable.toModelType();
        assertEquals(0, addressBook.getPersonList().size());
        assertEquals(0, addressBook.getClassSpaceList().size());
    }

    @Test
    public void toModelType_invalidClassSpaceAddressBook_skipsInvalidClassSpaceAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getClassSpaceList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid class space"));
    }

    /**
     * Helper method to read JSON file and assert that it produces "missing name" warning.
     *
     * @param filePath Path of JSON file.
     * @throws Exception
     */
    private void assertMissingNameWarningIsGenerated(Path filePath) throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(filePath,
                JsonSerializableAddressBook.class).get();
        dataFromFile.toModelType(); // This populates the warnings

        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("entry #1 (missing name)"));
        assertEquals(0, dataFromFile.toModelType().getPersonList().size());
    }
}

