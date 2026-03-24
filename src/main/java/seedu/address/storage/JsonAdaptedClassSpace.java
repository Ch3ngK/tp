package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Jackson-friendly version of {@link ClassSpace}.
 */
class JsonAdaptedClassSpace {

    // This String is not user-facing, so Class Space was not renamed to Group
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Class Space's %s field is missing!";

    private final String name;
    private final List<JsonAdaptedAssignment> assignments = new ArrayList<>();

    @JsonCreator
    public JsonAdaptedClassSpace(@JsonProperty("name") String name,
                                 @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments) {
        this.name = name;
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
    }

    public JsonAdaptedClassSpace(ClassSpace source) {
        name = source.getClassSpaceName().value;
        assignments.addAll(source.getAssignments().stream().map(JsonAdaptedAssignment::new).toList());
    }

    public ClassSpace toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    ClassSpaceName.class.getSimpleName()));
        }
        if (!ClassSpaceName.isValidClassSpaceName(name)) {
            throw new IllegalValueException(ClassSpaceName.MESSAGE_CONSTRAINTS);
        }

        List<Assignment> modelAssignments = new ArrayList<>();
        for (JsonAdaptedAssignment assignment : assignments) {
            modelAssignments.add(assignment.toModelType());
        }
        return new ClassSpace(new ClassSpaceName(name), modelAssignments);
    }
}
