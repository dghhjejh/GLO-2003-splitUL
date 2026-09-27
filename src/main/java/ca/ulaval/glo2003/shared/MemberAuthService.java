package ca.ulaval.glo2003.shared;

import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;

public class MemberAuthService {

    public static MemberAuthService instance;
    private static GroupPersistence groups;

    private MemberAuthService() {}

    public static MemberAuthService getInstance(GroupPersistence groupPersistence) {
        if (instance == null) {
            instance = new MemberAuthService();
            groups = groupPersistence;
        }
        return instance;
    }

    public void validateAuthentication(ContainerRequestContext requestContext, String groupName) {
        IGroup group = groups.getGroup(groupName);
        if (group == null) {
            throw new NotFoundException("Le groupe " + groupName + "n'existe pas");
        }

        String memberHeader = requestContext.getHeaderString("Member");

        if (
            memberHeader == null ||
            memberHeader.isEmpty() ||
            group == null ||
            !group.memberNameExists(memberHeader)
        ) {
            throw new ForbiddenException("Vous n'etes pas membre du groupe");
        }
    }
}
