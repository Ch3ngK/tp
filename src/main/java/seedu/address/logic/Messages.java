package seedu.address.logic;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
            "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for generic display to the user.
     * Uses the legacy person-level attendance and participation fields.
     */
    public static String format(Person person) {
        // TODO: Remove. This is legacy from pre-Session class.

        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Attendance: ")
                .append(person.getAttendance())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Matric Number: ")
                .append(person.getMatricNumber())
                .append("; Participation: ")
                .append(person.getParticipation())
                .append("; Tags: ");
        return getString(person, builder);
    }

    /**
     * Formats the {@code person} for a specific class space and session date.
     */
    public static String format(Person person, ClassSpaceName classSpaceName, LocalDate date) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Attendance: ")
                .append(person.getAttendance(classSpaceName, date))
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Matric Number: ")
                .append(person.getMatricNumber())
                .append("; Participation: ")
                .append(person.getParticipation(classSpaceName, date))
                .append("; Tags: ");
        return getString(person, builder);
    }

    private static String getString(Person person, StringBuilder builder) {
        person.getTags().forEach(builder::append);
        builder.append("; Class Spaces: ");
        builder.append(person.getClassSpaces().stream()
                .sorted(Comparator.comparing(classSpaceName -> classSpaceName.value, String.CASE_INSENSITIVE_ORDER))
                .map(classSpaceName -> classSpaceName.value)
                .collect(Collectors.joining(", ")));
        return builder.toString();
    }

}
