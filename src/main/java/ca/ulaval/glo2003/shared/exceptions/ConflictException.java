package ca.ulaval.glo2003.shared.exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class ConflictException extends ClientErrorException {

    public ConflictException(final String message) {
        super(message, Response.Status.CONFLICT);
    }
}
