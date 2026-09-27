package ca.ulaval.glo2003.shared.exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class InvalidActionException extends ClientErrorException {

    public InvalidActionException(final String message) {
        super(message, Response.Status.CONFLICT);
    }
}
