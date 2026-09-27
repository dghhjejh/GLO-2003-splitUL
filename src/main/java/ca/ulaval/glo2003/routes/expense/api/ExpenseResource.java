package ca.ulaval.glo2003.routes.expense.api;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseService;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.shared.exceptions.InvalidBodyException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.format.DateTimeParseException;

@Path("groups")
public class ExpenseResource {

    private final ExpenseService expenseService;

    public ExpenseResource(ExpensePersistence expensePersistence, GroupPersistence groupPersistence) {
        this.expenseService = new ExpenseService(expensePersistence, groupPersistence);
    }

    @POST
    @Path("/{groupName}/expenses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addExpenseToGroup(
        @PathParam("groupName") String groupName,
        ExpenseRequest body,
        @Context ContainerRequestContext requestContext
    ) {
        try {
            if (
                body == null ||
                body.description == null ||
                body.description.isEmpty() ||
                body.paidBy == null ||
                body.paidBy.isEmpty() ||
                body.split == null ||
                body.split.isEmpty() ||
                body.purchaseDate == null
            ) throw new InvalidBodyException("Le body de la requête est invalide.");
            return Response.created(
                expenseService.addExpenseToGroup(requestContext, groupName, body)
            ).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Une erreur est survenue lors de la création de l'URL.");
        } catch (DateTimeParseException e) {
            throw new BadRequestException("La date n'a pas le bon format. Utilisez le formats yyyy-MM-dd");
        }
    }
}
