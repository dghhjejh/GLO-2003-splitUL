package ca.ulaval.glo2003.shared.exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class InvalidBodyException extends ClientErrorException {

    public InvalidBodyException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }
}
