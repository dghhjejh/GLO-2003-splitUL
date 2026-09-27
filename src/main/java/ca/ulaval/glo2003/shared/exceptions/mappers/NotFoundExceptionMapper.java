package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse("ENTITY_NOT_FOUND", exception.getMessage());

        return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
    }
}
