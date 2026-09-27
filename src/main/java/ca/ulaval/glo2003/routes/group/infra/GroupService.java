package ca.ulaval.glo2003.routes.group.infra;

import ca.ulaval.glo2003.Main;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.shared.MemberAuthService;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.net.URI;
import java.util.ArrayList;

public class GroupService {

    private final GroupPersistence groupPersistence;
    private final MemberAuthService authService;

    public GroupService(GroupPersistence groupPersistence) {
        this.groupPersistence = groupPersistence;
        this.authService = MemberAuthService.getInstance(groupPersistence);
    }

    public ArrayList<IGroup> getGroups() {
        return groupPersistence.getGroups();
    }

    public IGroup getGroup(String groupName) {
        return groupPersistence.getGroup(groupName);
    }

    public URI addGroup(String newGroupName) {
        groupPersistence.verifyGroupExistsAlready(newGroupName);

        URI uriToReturn = URI.create(Main.BASE_URI + "groups/" + newGroupName);
        groupPersistence.addGroup(newGroupName);

        return uriToReturn;
    }

    public void deleteGroup(ContainerRequestContext requestContext, String groupName) {
        authService.validateAuthentication(requestContext, groupName);

        groupPersistence.deleteGroup(groupName);
    }

    public ExpenseHistoryDTO getGroupExpensesHistory(
        ContainerRequestContext requestContext,
        String groupName
    ) {
        authService.validateAuthentication(requestContext, groupName);

        return groupPersistence.getGroupExpenses(groupName);
    }

    public ExpenseHistoryDTO getGroupExpensesHistoryFilteredByMemberName(
        ContainerRequestContext requestContext,
        String groupName,
        String memberName
    ) {
        authService.validateAuthentication(requestContext, groupName);
        return groupPersistence.getGroupExpensesFilteredByMemberName(groupName, memberName);
    }

    public URI renameGroup(String oldGroupName, String newGroupName, String memberName) {
        if (!groupPersistence.groupExists(oldGroupName)) {
            throw new NotFoundException("Le groupe " + oldGroupName + " n'existe pas");
        }

        if (!groupPersistence.isMemberInGroup(memberName, oldGroupName)) {
            throw new ForbiddenException("Vous n'êtes pas membre du groupe");
        }

        if (newGroupName.contains(" ")) {
            throw new BadRequestException("Le nom contient des espaces");
        }

        if (groupPersistence.groupExists(newGroupName)) {
            throw new ConflictException("Le nom est déjà utilisé par un autre groupe");
        }

        groupPersistence.renameGroup(oldGroupName, newGroupName);
        return URI.create(Main.BASE_URI + "groups/" + newGroupName);
    }
}
