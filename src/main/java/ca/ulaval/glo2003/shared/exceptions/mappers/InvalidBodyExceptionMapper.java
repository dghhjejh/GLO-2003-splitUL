package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import ca.ulaval.glo2003.shared.exceptions.InvalidBodyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidBodyExceptionMapper implements ExceptionMapper<InvalidBodyException> {

    @Override
    public Response toResponse(InvalidBodyException exception) {
        ErrorResponse errorResponse = new ErrorResponse("INVALID_BODY", exception.getMessage());

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }
}
