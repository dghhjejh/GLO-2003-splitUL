package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(ConflictException exception) {
        ErrorResponse errorResponse = new ErrorResponse("CONFLICTING_PARAMETER", exception.getMessage());

        return Response.status(Response.Status.CONFLICT).entity(errorResponse).build();
    }
}
