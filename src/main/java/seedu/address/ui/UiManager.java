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
    private static final String SAVE_FILE_MESSAGE = "\nYou can fix these entries directly in the save file: ";

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

    private String buildStartUpMessage(List<String> warnings) {
        if (warnings.isEmpty()) {
            return buildSuccessMessage();
        }
        return buildWarningHeader(warnings.size())
                + buildWarningList(warnings)
                + buildWarningFooter();
    }

    private String buildWarningHeader(int skippedCount) {
        int loadedCount = logic.getFilteredPersonList().size();
        return loadedCount + (loadedCount == 1 ? " contact" : " contacts") + " loaded successfully. "
                + skippedCount + (skippedCount == 1 ? " contact" : " contacts")
                + " could not be loaded and "
                + (skippedCount == 1 ? "was" : "were")
                + " skipped:\n\n";
    }

    private String buildWarningList(List<String> warnings) {
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < warnings.size(); i++) {
            list.append(i + 1).append(". ").append(warnings.get(i)).append("\n");
        }
        return list.toString();
    }

    private String buildWarningFooter() {
        return SAVE_FILE_MESSAGE + logic.getAddressBookFilePath();
    }

    private String buildSuccessMessage() {
        int count = logic.getFilteredPersonList().size();
        return count + (count == 1 ? " contact" : " contacts") + " loaded successfully.";
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
