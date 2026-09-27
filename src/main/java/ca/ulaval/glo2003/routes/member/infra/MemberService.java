package ca.ulaval.glo2003.routes.member.infra;

import ca.ulaval.glo2003.Main;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.shared.MemberAuthService;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.net.URI;
import java.util.ArrayList;

public class MemberService {

    private final MemberPersistence memberPersistence;
    private final MemberAuthService authService;

    public MemberService(MemberPersistence memberPersistence, GroupPersistence groupPersistence) {
        this.memberPersistence = memberPersistence;
        this.authService = MemberAuthService.getInstance(groupPersistence);
    }

    public URI addMemberToGroup(String groupName, String newMemberName) {
        memberPersistence.verifyMemberExistAlready(groupName, newMemberName);
        URI location = URI.create(Main.BASE_URI + "groups/" + groupName + "/members/" + newMemberName);
        memberPersistence.addMember(groupName, newMemberName);

        return location;
    }

    public ArrayList<IMember> getGroupMembers(ContainerRequestContext requestContext, String groupName) {
        authService.validateAuthentication(requestContext, groupName);

        return memberPersistence.getMembers(groupName);
    }

    public void settleMemberDebt(
        ContainerRequestContext requestContext,
        String groupName,
        String indebtedMemberName,
        String debtToPayToMemberName
    ) {
        authService.validateAuthentication(requestContext, groupName);

        memberPersistence.settleDebt(groupName, indebtedMemberName, debtToPayToMemberName);
    }

    public URI changeMemberName(
        ContainerRequestContext requestContext,
        String groupName,
        String currentMemberName,
        String headerCurrentMemberName,
        String newMemberName
    ) {
        authService.validateAuthentication(requestContext, groupName);
        verifyMemberAndHeaderMemberAreSame(currentMemberName, headerCurrentMemberName);
        memberPersistence.verifyMemberExistAlready(groupName, newMemberName);

        URI location = URI.create(Main.BASE_URI + "groups/" + groupName + "/members/" + newMemberName);
        memberPersistence.changeMemberName(groupName, currentMemberName, newMemberName);

        return location;
    }

    private void verifyMemberAndHeaderMemberAreSame(String memberName, String headerMemberName) {
        if (!memberName.equals(headerMemberName)) {
            throw new ConflictException(
                "Le nom" +
                memberName +
                "n'est pas le même que dans le Header Membre (" +
                headerMemberName +
                ")"
            );
        }
    }
}
