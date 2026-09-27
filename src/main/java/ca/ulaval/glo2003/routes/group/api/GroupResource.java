package ca.ulaval.glo2003.routes.group.api;

import ca.ulaval.glo2003.routes.group.infra.GroupService;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import ca.ulaval.glo2003.shared.exceptions.ErrorResponse;
import ca.ulaval.glo2003.shared.exceptions.InvalidBodyException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("groups")
public class GroupResource {

    private final GroupService groupService;

    public GroupResource(GroupPersistence groupPersistence) {
        groupService = new GroupService(groupPersistence);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups() {
        return Response.ok(groupService.getGroups()).build();
    }

    @GET
    @Path("/{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroup(@PathParam("groupName") String groupName) {
        return Response.ok(groupService.getGroup(groupName)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response groups(GroupRequest newGroup) {
        try {
            if (
                newGroup == null || newGroup.name == null || newGroup.name.isEmpty()
            ) throw new InvalidBodyException("Le body de la requête est inexistant ou invalide.");
            return Response.created(groupService.addGroup(newGroup.name)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Le nom du groupe contient un ou des espaces");
        }
    }

    @DELETE
    @Path("{groupName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGroup(
        @PathParam("groupName") String groupName,
        @Context ContainerRequestContext requestContext
    ) {
        groupService.deleteGroup(requestContext, groupName);
        return Response.noContent().build();
    }

    @GET
    @Path("/{groupName}/expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupExpensesHistory(
        @PathParam("groupName") String groupName,
        @Context ContainerRequestContext requestContext
    ) {
        return Response.ok(groupService.getGroupExpensesHistory(requestContext, groupName)).build();
    }

    @GET
    @Path("/{groupName}/expenses/{memberName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterGroupExpensesHistoryByMemberName(
        @PathParam("groupName") String groupName,
        @PathParam("memberName") String memberName,
        @Context ContainerRequestContext requestContext
    ) {
        return Response.ok(
            groupService.getGroupExpensesHistoryFilteredByMemberName(requestContext, groupName, memberName)
        ).build();
    }

    @PUT
    @Path("/{groupName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameGroup(
        @PathParam("groupName") String oldGroupName,
        @HeaderParam("Member") String memberName,
        RenameGroupRequest request
    ) {
        try {
            if (request == null || request.newName == null || request.newName.trim().isEmpty()) {
                throw new BadRequestException("Le body est vide ou invalide");
            }

            URI location = groupService.renameGroup(oldGroupName, request.newName, memberName);
            return Response.ok().location(location).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse("INTERDIT", "Vous n'êtes pas membre du groupe"))
                .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(
                    new ErrorResponse("ENTITÉ_NON_TROUVÉE", "Le groupe " + oldGroupName + " n'existe pas")
                )
                .build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("BAD_REQUEST", e.getMessage()))
                .build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity(
                    new ErrorResponse("CONFLICTING_PARAMETER", "Le nom est déjà utilisé par un autre groupe")
                )
                .build();
        }
    }
}
