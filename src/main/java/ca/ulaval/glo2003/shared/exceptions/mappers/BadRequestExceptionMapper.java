package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException exception) {
        ErrorResponse errorResponse = new ErrorResponse("INVALID_PARAMETER", exception.getMessage());

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }
}
