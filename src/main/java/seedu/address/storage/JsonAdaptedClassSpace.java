package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Jackson-friendly version of {@link ClassSpace}.
 */
class JsonAdaptedClassSpace {

    // This String is not user-facing, so Class Space was not renamed to Group
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Class Space's %s field is missing!";

    private final String name;

    @JsonCreator
    public JsonAdaptedClassSpace(@JsonProperty("name") String name) {
        this.name = name;
    }

    public JsonAdaptedClassSpace(ClassSpace source) {
        name = source.getClassSpaceName().value;
    }

    public ClassSpace toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    ClassSpaceName.class.getSimpleName()));
        }
        if (!ClassSpaceName.isValidClassSpaceName(name)) {
            throw new IllegalValueException(ClassSpaceName.MESSAGE_CONSTRAINTS);
        }
        return new ClassSpace(new ClassSpaceName(name));
    }
}
