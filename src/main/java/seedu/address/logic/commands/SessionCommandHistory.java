package seedu.address.logic.commands;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classspace.ClassSpaceName;

/**
 * Stores the previous state for session-specific undo.
 */
public final class SessionCommandHistory {
    private static SessionCommandSnapshot lastSnapshot;

    private SessionCommandHistory() {}

    /**
     * Records the current model state so the last session-related mutation can be undone.
     */
    public static void record(Model model, String description) {
        lastSnapshot = new SessionCommandSnapshot(
                new AddressBook(model.getAddressBook()),
                model.getActiveClassSpaceName(),
                model.getActiveSessionDate(),
                model.isAttendanceViewActive(),
                model.getVisibleSessionRangeStart(),
                model.getVisibleSessionRangeEnd(),
                description);
    }

    public static Optional<SessionCommandSnapshot> getLastSnapshot() {
        return Optional.ofNullable(lastSnapshot);
    }

    public static void clear() {
        lastSnapshot = null;
    }

    /**
     * Snapshot of the state needed to undo a session-related command.
     */
    public record SessionCommandSnapshot(ReadOnlyAddressBook addressBook,
                                         Optional<ClassSpaceName> activeClassSpaceName,
                                         Optional<LocalDate> activeSessionDate,
                                         boolean attendanceViewActive,
                                         Optional<LocalDate> visibleRangeStart,
                                         Optional<LocalDate> visibleRangeEnd,
                                         String description) {}
}
