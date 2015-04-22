package org.aurorawatchdevs.aurorawatch.exception;

/**
 * Thrown when the activity.txt file could not be parsed.
 */
public class InvalidActivityTxtException extends IllegalArgumentException {

    public InvalidActivityTxtException() {
        super();
    }

    public InvalidActivityTxtException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidActivityTxtException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidActivityTxtException(Throwable cause) {
        super(cause);
    }
}
