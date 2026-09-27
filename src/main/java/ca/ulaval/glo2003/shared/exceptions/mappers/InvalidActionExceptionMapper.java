package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import ca.ulaval.glo2003.shared.exceptions.InvalidActionException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidActionExceptionMapper implements ExceptionMapper<InvalidActionException> {

    @Override
    public Response toResponse(InvalidActionException exception) {
        ErrorResponse errorResponse = new ErrorResponse("INVALID_ACTION", exception.getMessage());

        return Response.status(Response.Status.CONFLICT).entity(errorResponse).build();
    }
}
