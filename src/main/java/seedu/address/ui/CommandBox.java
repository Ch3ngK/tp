package seedu.address.ui;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandRegistry;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final List<String> COMMAND_SUGGESTIONS = CommandRegistry.COMMAND_WORDS;
    // TODO: Use AddressBookParser.getCommandWords() instead to reduce duplication

    private final CommandExecutor commandExecutor;

    @FXML
    private TextArea commandTextField; // TODO: Rename this to Area without breaking downstream

    @FXML
    private Label ghostTextLabel;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        commandTextField.textProperty().addListener((unused1, unused2, newText) -> {
            setStyleToDefault();
            updateGhostText(newText);
        });
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isBlank()) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
            clearGhostText();
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles the Enter button pressed event for TextArea,
     * to handle migration from TextField to TextArea,
     * to support resizeable panels.
     */
    @FXML
    private void initialize() {
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // handle ENTER key
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                handleCommandEntered();
            }

            // handle TAB key
            if (event.getCode() == KeyCode.TAB) {
                event.consume(); // Prevent focus from shifting to another element
                String suggestion = ghostTextLabel.getText();
                if (!suggestion.isBlank()) {
                    commandTextField.setText(suggestion + " ");
                    commandTextField.positionCaret(commandTextField.getText().length());
                    clearGhostText();
                }
            }
        });
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Updates the faded ghost text suggestion based on the current input.
     */
    private void updateGhostText(String input) {
        if (input == null || input.isBlank()) {
            clearGhostText();
            return;
        }

        if (input.contains("\n")) {
            clearGhostText();
            return;
        }

        String trimmedInput = input.stripLeading();
        if (trimmedInput.isEmpty() || trimmedInput.contains(" ")) {
            clearGhostText();
            return;
        }

        String suggestion = findSuggestion(trimmedInput.toLowerCase());
        if (suggestion == null || suggestion.equals(trimmedInput.toLowerCase())) {
            clearGhostText();
            return;
        }

        ghostTextLabel.setText(suggestion);
        ghostTextLabel.setVisible(true);
    }

    /**
     * Returns the first matching command suggestion for the input.
     */
    private String findSuggestion(String input) {
        for (String command : COMMAND_SUGGESTIONS) {
            if (command.startsWith(input)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Clears the ghost text suggestion.
     */
    private void clearGhostText() {
        ghostTextLabel.setText("");
        ghostTextLabel.setVisible(false);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
