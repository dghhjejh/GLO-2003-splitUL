package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(ForbiddenException exception) {
        ErrorResponse errorResponse = new ErrorResponse("FORBIDDEN", exception.getMessage());

        return Response.status(Response.Status.FORBIDDEN).entity(errorResponse).build();
    }
}
