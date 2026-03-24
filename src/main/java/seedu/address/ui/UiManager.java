package seedu.address.ui;

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;

/**
 * The manager of the UI component.
 */
public class UiManager implements Ui {

    public static final String ALERT_DIALOG_PANE_FIELD_ID = "alertDialogPane";

    private static final Logger logger = LogsCenter.getLogger(UiManager.class);
    private static final String ICON_APPLICATION = "/images/address_book_32.png";
    private static final String INVALID_CONTACT_PREFIX = "Skipped invalid contact";
    private static final String DUPLICATE_CONTACT_PREFIX = "Skipped duplicate contact";
    private static final String INVALID_CLASS_SPACE_PREFIX = "Skipped invalid class space";
    private static final String DUPLICATE_CLASS_SPACE_PREFIX = "Skipped duplicate class space";
    private static final String LINE_SEPARATOR = "\n";
    private static final String FATAL_PREFIX = "FATAL:";

    private Logic logic;
    private MainWindow mainWindow;
    private final List<String> startUpWarnings;

    /**
     * Creates a {@code UiManager} with the given {@code Logic}.
     */
    public UiManager(Logic logic) {
        this(logic, List.of());
    }

    /**
     * Creates a {@code UiManager} with the given {@code Logic} and startup warnings
     * to display in the result panel once the UI is ready.
     *
     * @param logic The given {@code Logic}.
     * @param startupWarnings The list of startup warnings.
     */
    public UiManager(Logic logic, List<String> startupWarnings) {
        this.logic = logic;
        this.startUpWarnings = List.copyOf(startupWarnings);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting UI...");

        //Set the application icon.
        primaryStage.getIcons().add(getImage(ICON_APPLICATION));

        try {
            mainWindow = new MainWindow(primaryStage, logic);
            mainWindow.show(); //This should be called before creating other UI parts
            mainWindow.fillInnerParts();
            displayStartUpWarnings();

        } catch (Throwable e) {
            logger.severe(StringUtil.getDetails(e));
            showFatalErrorDialogAndShutdown("Fatal error during initializing", e);
        }
    }

    private void displayStartUpWarnings() {
        mainWindow.getResultDisplay().setFeedbackToUser(buildStartUpMessage(startUpWarnings));
    }

    String buildStartUpMessage(List<String> warnings) {
        StringBuilder message = new StringBuilder(buildLoadedContactsMessage());

        appendFatalErrorSection(message, warnings);
        appendContactWarningSection(message, warnings);
        appendClassSpaceWarningSection(message, warnings);

        return message.toString();
    }

    private void appendFatalErrorSection(StringBuilder message, List<String> warnings) {
        List<String> fatalWarnings = warnings.stream()
                .filter(w -> w.startsWith(FATAL_PREFIX))
                .toList();
        if (fatalWarnings.isEmpty()) {
            return;
        }
        appendSectionSpacing(message);
        // Strip the prefix for display
        message.append(fatalWarnings.get(0).substring(FATAL_PREFIX.length()).trim());
    }


    private String buildLoadedContactsMessage() {
        int loadedContacts = logic.getAddressBook().getPersonList().size();
        return String.format("%d contacts loaded successfully.", loadedContacts);
    }

    private void appendContactWarningSection(StringBuilder message, List<String> warnings) {
        List<String> contactWarnings = getContactWarnings(warnings);
        if (contactWarnings.isEmpty()) {
            return;
        }

        appendSectionSpacing(message);
        message.append(buildSkippedSection("contact", contactWarnings));
    }

    private void appendClassSpaceWarningSection(StringBuilder message, List<String> warnings) {
        List<String> classSpaceWarnings = getClassSpaceWarnings(warnings);
        if (classSpaceWarnings.isEmpty()) {
            return;
        }

        appendSectionSpacing(message);
        message.append(buildSkippedSection("class space", classSpaceWarnings));
    }

    /**
     * Returns only the warnings related to contacts.
     *
     * @param warnings All warnings collected during loading.
     * @return Contact-related warnings.
     */
    private List<String> getContactWarnings(List<String> warnings) {
        return warnings.stream()
                .filter(this::isContactWarning)
                .toList();
    }

    /**
     * Returns only the warnings related to class spaces.
     *
     * @param warnings All warnings collected during loading.
     * @return Class-space-related warnings.
     */
    private List<String> getClassSpaceWarnings(List<String> warnings) {
        return warnings.stream()
                .filter(this::isClassSpaceWarning)
                .toList();
    }

    /**
     * Returns true if the warning is related to a contact.
     *
     * @param warning Warning message to check.
     * @return True if the warning is a contact warning.
     */
    private boolean isContactWarning(String warning) {
        return warning.startsWith(INVALID_CONTACT_PREFIX)
                || warning.startsWith(DUPLICATE_CONTACT_PREFIX);
    }

    /**
     * Returns true if the warning is related to a class space.
     *
     * @param warning Warning message to check.
     * @return True if the warning is a class space warning.
     */
    private boolean isClassSpaceWarning(String warning) {
        return warning.startsWith(INVALID_CLASS_SPACE_PREFIX)
                || warning.startsWith(DUPLICATE_CLASS_SPACE_PREFIX);
    }

    /**
     * Builds one skipped-entry section, such as contacts or class spaces.
     *
     * @param itemType Type of item skipped.
     * @param warnings Warnings for that item type.
     * @return Formatted skipped-entry section.
     */
    private String buildSkippedSection(String itemType, List<String> warnings) {
        return buildSkippedSummary(itemType, warnings.size())
                + LINE_SEPARATOR
                + buildWarningList(warnings);
    }

    /**
     * Builds the summary line for one skipped-entry section.
     *
     * @param itemType Type of item skipped.
     * @param count Number of skipped items.
     * @return Summary line for the section.
     */
    private String buildSkippedSummary(String itemType, int count) {
        if (count == 1) {
            return String.format("1 %s could not be loaded and was skipped:", itemType);
        }

        return String.format("%d %ss could not be loaded and were skipped:", count, itemType);
    }

    /**
     * Builds a numbered list of warnings.
     *
     * @param warnings Warnings to format.
     * @return Numbered warning list.
     */
    String buildWarningList(List<String> warnings) {
        StringBuilder list = new StringBuilder();

        for (int i = 0; i < warnings.size(); i++) {
            appendNumberedWarning(list, i + 1, warnings.get(i));
            appendWarningSpacing(list, i, warnings.size());
        }

        return list.toString();
    }

    /**
     * Appends one numbered warning entry.
     *
     * @param list StringBuilder holding the warning list.
     * @param number Display number for the warning.
     * @param warning Warning message content.
     */
    private void appendNumberedWarning(StringBuilder list, int number, String warning) {
        list.append(number)
                .append(". ")
                .append(warning);
    }

    /**
     * Appends spacing after a warning entry.
     *
     * @param list StringBuilder holding the warning list.
     * @param currentIndex Index of the current warning.
     * @param totalWarnings Total number of warnings.
     */
    private void appendWarningSpacing(StringBuilder list, int currentIndex, int totalWarnings) {
        if (currentIndex < totalWarnings - 1) {
            list.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
            return;
        }

        list.append(LINE_SEPARATOR);
    }

    /**
     * Appends spacing before a new warning section.
     *
     * @param message The startup message being built.
     */
    private void appendSectionSpacing(StringBuilder message) {
        message.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
    }

    private Image getImage(String imagePath) {
        return new Image(MainApp.class.getResourceAsStream(imagePath));
    }

    void showAlertDialogAndWait(Alert.AlertType type, String title, String headerText, String contentText) {
        showAlertDialogAndWait(mainWindow.getPrimaryStage(), type, title, headerText, contentText);
    }

    /**
     * Shows an alert dialog on {@code owner} with the given parameters.
     * This method only returns after the user has closed the alert dialog.
     */
    private static void showAlertDialogAndWait(Stage owner, AlertType type, String title, String headerText,
                                               String contentText) {
        final Alert alert = new Alert(type);
        alert.getDialogPane().getStylesheets().add("view/DarkTheme.css");
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setId(ALERT_DIALOG_PANE_FIELD_ID);
        alert.showAndWait();
    }

    /**
     * Shows an error alert dialog with {@code title} and error message, {@code e},
     * and exits the application after the user has closed the alert dialog.
     */
    private void showFatalErrorDialogAndShutdown(String title, Throwable e) {
        logger.severe(title + " " + e.getMessage() + StringUtil.getDetails(e));
        showAlertDialogAndWait(Alert.AlertType.ERROR, title, e.getMessage(), e.toString());
        Platform.exit();
        System.exit(1);
    }
    /**
     * Displays a startup warning message in the result display after the UI has started.
     * This is used to inform the user about any issues with the saved data file.
     *
     * @param message The warning message to display to the user.
     */
    public void showStartUpWarning(String message) {
        Platform.runLater(() -> mainWindow.showStartupWarning(message));
    }

}
