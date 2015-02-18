package uk.ac.lancs.aurorawatch.exception;


/**
 * Base class for exceptions thrown when data received from the AuroraWatch UK
 * API does not match the expected format.
 */
public abstract class InvalidDataException extends IllegalArgumentException {

    public InvalidDataException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    public InvalidDataException() {
        super();
    }
}
