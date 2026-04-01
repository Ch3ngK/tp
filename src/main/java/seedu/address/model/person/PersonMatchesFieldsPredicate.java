package seedu.address.model.person;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.model.tag.Tag;

/**
 * Tests whether a {@code Person} matches any fields given in search parameters.
 * Matching is case-insensitive.
 */
public class PersonMatchesFieldsPredicate implements Predicate<Person> {

    private final List<String> names;
    private final List<String> phones;
    private final List<String> emails;
    private final List<String> matricNumbers;
    private final List<String> tags;

    /**
     * Constructs a {@code PersonMatchesFieldsPredicate} with the given search parameters.
     *
     * @param names List of name search keywords.
     * @param phones List of phone search keywords.
     * @param emails List of email search keywords.
     * @param matricNumbers List of matriculation number search keywords.
     * @param tags List of tag search keywords.
     */
    public PersonMatchesFieldsPredicate(List<String> names, List<String> phones, List<String> emails,
                                        List<String> matricNumbers, List<String> tags) {
        this.names = List.copyOf(names);
        this.phones = List.copyOf(phones);
        this.emails = List.copyOf(emails);
        this.matricNumbers = List.copyOf(matricNumbers);
        this.tags = List.copyOf(tags);
    }

    /**
     * Returns whether the given person matches at least one search parameter.
     *
     * @param person The person to test.
     * @return true if the person matches at least one search parameter false otherwise.
     */
    @Override
    public boolean test(Person person) {
        return getMatchedCriteriaCount(person) > 0;
    }

    /**
     * Returns the number of search parameters matched by this person.
     *
     * @param person The person whose fields are to be checked.
     * @return Number of search parameters the person matches.
     */
    public int getMatchedCriteriaCount(Person person) {
        int count = 0;

        String personName = person.getName().toString().toLowerCase();
        String personPhone = person.getPhone().toString().toLowerCase();
        String personEmail = person.getEmail().toString().toLowerCase();
        String personMatricNumber = person.getMatricNumber().toString().toLowerCase();
        Set<Tag> personTags = person.getTags();

        for (String keyword : names) {
            if (personName.contains(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : phones) {
            if (personPhone.contains(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : emails) {
            if (personEmail.contains(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : matricNumbers) {
            if (personMatricNumber.contains(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : tags) {
            if (hasMatchingTag(personTags, keyword)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the number of search parameters that matched exactly.
     *
     * @param person The person whose fields are to be checked.
     * @return Number of search parameters the person matches exactly.
     */
    public int getExactMatchCount(Person person) {
        int count = 0;

        String personName = person.getName().toString().toLowerCase();
        String personPhone = person.getPhone().toString().toLowerCase();
        String personEmail = person.getEmail().toString().toLowerCase();
        String personMatricNumber = person.getMatricNumber().toString().toLowerCase();
        Set<Tag> personTags = person.getTags();

        for (String keyword : names) {
            if (personName.equals(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : phones) {
            if (personPhone.equals(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : emails) {
            if (personEmail.equals(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : matricNumbers) {
            if (personMatricNumber.equals(keyword.toLowerCase())) {
                count++;
            }
        }

        for (String keyword : tags) {
            if (hasExactTag(personTags, keyword)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns whether a tag for the given person matches the given parameters partially.
     *
     * @param personTags The set of tags belonging to a person.
     * @param keyword The search keyword to match against the tags.
     * @return true if a tag matches the search parameters partially false otherwise.
     */
    private boolean hasMatchingTag(Set<Tag> personTags, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return personTags.stream()
                .map(tag -> tag.tagName)
                .map(String::toLowerCase)
                .anyMatch(tag -> tag.contains(lowerKeyword));
    }

    /**
     * Returns whether a tag for the given person matches the given parameters exactly.
     *
     * @param personTags The set of tags belonging to a person.
     * @param keyword The search keyword to match against the tags.
     * @return true if a tag matches the search parameters exactly false otherwise.
     */
    private boolean hasExactTag(Set<Tag> personTags, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return personTags.stream()
                .map(tag -> tag.tagName)
                .map(String::toLowerCase)
                .anyMatch(tag -> tag.equals(lowerKeyword));
    }

    /**
     * Returns true if both predicates are identical.
     * This defines a stronger notion of equality between two predicates.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PersonMatchesFieldsPredicate)) {
            return false;
        }

        PersonMatchesFieldsPredicate otherPredicate = (PersonMatchesFieldsPredicate) other;
        return names.equals(otherPredicate.names)
                && phones.equals(otherPredicate.phones)
                && emails.equals(otherPredicate.emails)
                && matricNumbers.equals(otherPredicate.matricNumbers)
                && tags.equals(otherPredicate.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(names, phones, emails, matricNumbers, tags);
    }
}
