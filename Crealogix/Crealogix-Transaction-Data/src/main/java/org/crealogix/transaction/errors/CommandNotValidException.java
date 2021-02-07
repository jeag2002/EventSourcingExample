package org.crealogix.transaction.errors;

import org.crealogix.infraestructure.interfaces.exceptions.CommandException;

public class CommandNotValidException extends CommandException {

    public CommandNotValidException(final String message) {
        super(message);
    }

    public CommandNotValidException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
