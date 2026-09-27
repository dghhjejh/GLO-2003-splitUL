package ca.ulaval.glo2003.routes.member.api;

import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.member.infra.MemberService;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.shared.exceptions.InvalidBodyException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("groups")
public class MemberResource {

    private final MemberService memberService;

    public MemberResource(MemberPersistence memberPersistence, GroupPersistence groupPersistence) {
        this.memberService = new MemberService(memberPersistence, groupPersistence);
    }

    @POST
    @Path("/{groupName}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMemberToGroup(@PathParam("groupName") String groupName, MemberRequest newMember) {
        try {
            if (
                newMember == null || newMember.memberName == null || newMember.memberName.isEmpty()
            ) throw new InvalidBodyException("Le body de la requête est inexistant ou invalide.");
            return Response.created(memberService.addMemberToGroup(groupName, newMember.memberName)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Le nom du membre contient un ou des espaces");
        }
    }

    @GET
    @Path("/{groupName}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listGroupMembers(
        @PathParam("groupName") String groupName,
        @Context ContainerRequestContext requestContext
    ) {
        return Response.ok(memberService.getGroupMembers(requestContext, groupName)).build();
    }

    @PUT
    @Path("/{groupName}/members/{memberName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeMemberName(
        @PathParam("groupName") String groupName,
        @PathParam("memberName") String currentMemberName,
        @HeaderParam("Member") String headerCurrentMemberName,
        @Context ContainerRequestContext requestContext,
        MemberUpdateRequest memberUpdateRequest
    ) {
        try {
            return Response.created(
                memberService.changeMemberName(
                    requestContext,
                    groupName,
                    currentMemberName,
                    headerCurrentMemberName,
                    memberUpdateRequest.newMemberName
                )
            ).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Le nom du membre contient un ou des espaces");
        }
    }

    @PUT
    @Path("/{groupName}/members/{memberName}/settle")
    @Produces(MediaType.APPLICATION_JSON)
    public Response settleDebtToMember(
        @PathParam("groupName") String groupName,
        @PathParam("memberName") String debtToPayToMemberName,
        @HeaderParam("Member") String indebtedMemberName,
        @Context ContainerRequestContext requestContext
    ) {
        memberService.settleMemberDebt(requestContext, groupName, indebtedMemberName, debtToPayToMemberName);
        return Response.noContent().build();
    }
}
