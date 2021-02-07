package org.crealogix.transaction.errors;

import org.crealogix.infraestructure.interfaces.exceptions.EventException;

public class InconsistentStateException extends EventException {

    public InconsistentStateException(final String message) {
        super(message);
    }

    public InconsistentStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
