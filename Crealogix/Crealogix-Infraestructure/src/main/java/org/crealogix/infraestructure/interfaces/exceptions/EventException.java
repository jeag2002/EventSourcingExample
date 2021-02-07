package org.crealogix.infraestructure.interfaces.exceptions;

public class EventException extends RuntimeException {

    public EventException(final String message) {
        super(message);
    }

    public EventException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
