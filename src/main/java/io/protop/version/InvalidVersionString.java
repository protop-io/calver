package io.protop.version;

/**
 * Thrown when a string cannot be evaluated as a calendar version.
 */
public final class InvalidVersionString extends Exception {

    public InvalidVersionString(String message) {
        super(message);
    }
}
