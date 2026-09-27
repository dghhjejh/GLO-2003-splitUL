package ca.ulaval.glo2003.shared.exceptions.mappers;

import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import io.sentry.Sentry;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (!(exception instanceof jakarta.ws.rs.WebApplicationException)) {
            Sentry.captureException(exception);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Une erreur interne est survenue")
            .build();
    }
}
