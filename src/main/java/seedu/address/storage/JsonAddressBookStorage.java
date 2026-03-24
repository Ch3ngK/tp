package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access app data stored as a JSON file on the hard disk.
 * Stores warnings encountered during the loading process and provides them to other components.
 */
public class JsonAddressBookStorage implements AddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private Path filePath;

    private List<String> lastLoadWarnings = new ArrayList<>();
    private List<JsonNode> lastSkippedPersons = new ArrayList<>();
    private List<JsonNode> lastSkippedClassSpaces = new ArrayList<>();
    private boolean shouldSkipSaveAfterFatalLoad = false;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the path where the app file is stored.
     *
     * @return The path to the app file.
     */
    public Path getAddressBookFilePath() {
        return filePath;
    }

    /**
     * Returns a list of warnings encountered during the last reading of the app.
     *
     * @return An unmodifiable list of warnings encountered during the last load operation.
     */
    public List<String> getLastLoadWarnings() {
        return Collections.unmodifiableList(lastLoadWarnings);
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        lastLoadWarnings = new ArrayList<>();
        lastSkippedPersons = new ArrayList<>();
        lastSkippedClassSpaces = new ArrayList<>();
        shouldSkipSaveAfterFatalLoad = false;
        try {
            Optional<JsonSerializableAddressBook> jsonAddressBook = JsonUtil.readJsonFile(
                    filePath, JsonSerializableAddressBook.class);
            if (!jsonAddressBook.isPresent()) {
                return Optional.empty();
            }
            JsonSerializableAddressBook serializable = jsonAddressBook.get();
            ReadOnlyAddressBook result = serializable.toModelType();
            lastLoadWarnings.addAll(serializable.getLoadWarnings());
            lastSkippedPersons.addAll(serializable.getPreservedSkippedPersons());
            lastSkippedClassSpaces.addAll(serializable.getPreservedSkippedClassSpaces());
            return Optional.of(result);
        } catch (IllegalValueException | DataLoadingException e) {
            shouldSkipSaveAfterFatalLoad = true;
            throw new DataLoadingException(e);
        }
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        if (shouldSkipSaveAfterFatalLoad) {
            logger.warning("Skipping save because the last load failed fatally. "
                    + "This prevents overwriting the original save file.");
            return;
        }

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook, lastSkippedPersons,
                lastSkippedClassSpaces, lastLoadWarnings), filePath);
    }

}
