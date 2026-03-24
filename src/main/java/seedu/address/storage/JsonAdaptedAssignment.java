package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;

/**
 * Jackson-friendly version of {@link Assignment}.
 */
class JsonAdaptedAssignment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Assignment's %s field is missing!";

    private final String name;
    private final String dueDate;
    private final Integer maxMarks;

    @JsonCreator
    public JsonAdaptedAssignment(@JsonProperty("name") String name,
                                 @JsonProperty("dueDate") String dueDate,
                                 @JsonProperty("maxMarks") Integer maxMarks) {
        this.name = name;
        this.dueDate = dueDate;
        this.maxMarks = maxMarks;
    }

    public JsonAdaptedAssignment(Assignment source) {
        name = source.getAssignmentName().value;
        dueDate = source.getDueDate().toString();
        maxMarks = source.getMaxMarks();
    }

    public Assignment toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    AssignmentName.class.getSimpleName()));
        }
        if (!AssignmentName.isValidAssignmentName(name)) {
            throw new IllegalValueException(AssignmentName.MESSAGE_CONSTRAINTS);
        }
        if (dueDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "dueDate"));
        }
        if (maxMarks == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "maxMarks"));
        }

        try {
            return new Assignment(new AssignmentName(name), java.time.LocalDate.parse(dueDate), maxMarks);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalValueException(seedu.address.model.person.Session.MESSAGE_CONSTRAINTS);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
}
