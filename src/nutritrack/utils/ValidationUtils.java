package nutritrack.utils;

/**
 * Common validation utilities for user input fields.
 * Used across UI panels and Service layers to maintain clean data.
 */
public class ValidationUtils {

    private ValidationUtils() { }

    /**
     * Validates that a string is not null and not empty after trimming.
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Parses a string to a positive double (> 0).
     *
     * @return parsed double or throws IllegalArgumentException
     */
    public static double parsePositiveDouble(String input, String fieldName) {
        if (!isNotEmpty(input)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        try {
            double value = Double.parseDouble(input.trim());
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than zero.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid numeric number.");
        }
    }

    /**
     * Parses a string to a non-negative double (>= 0).
     */
    public static double parseNonNegativeDouble(String input, String fieldName) {
        if (!isNotEmpty(input)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        try {
            double value = Double.parseDouble(input.trim());
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid numeric number.");
        }
    }
}
