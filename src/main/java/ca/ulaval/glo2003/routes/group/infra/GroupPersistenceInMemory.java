package ca.ulaval.glo2003.routes.group.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.group.logic.Group;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import ca.ulaval.glo2003.shared.exceptions.InvalidActionException;
import ca.ulaval.glo2003.shared.infra.Groups;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;

public class GroupPersistenceInMemory implements GroupPersistence {

    private final Groups groups = Groups.getInstance();

    public ArrayList<IGroup> getGroups() {
        return groups.getGroups();
    }

    public IGroup getGroup(String groupName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);

        if (existingGroup != null) return existingGroup;

        throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
    }

    public void verifyGroupExistsAlready(String groupName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);

        if (existingGroup != null) throw new ConflictException(
            "Le nom du groupe " + groupName + " existe deja"
        );
    }

    public void addGroup(String newGroupName) {
        groups.getGroups().add(new Group(newGroupName, new ArrayList<>(), new ArrayList<>()));
    }

    public void deleteGroup(String groupName) {
        IGroup groupToDelete = groups.getExistingGroup(groupName);

        if (groupToDelete.hasUnsettledDebts()) {
            throw new InvalidActionException(
                "Le groupe ne peut pas être supprimé, certaines dettes ne sont pas réglées"
            );
        }

        groups.getGroups().remove(groupToDelete);
    }

    public boolean groupExists(String groupName) {
        return groups.getExistingGroup(groupName) != null;
    }

    public boolean isMemberInGroup(String memberName, String groupName) {
        Group group = (Group) groups.getExistingGroup(groupName);
        if (group == null) {
            return false;
        }
        return group.getMembers().stream().anyMatch(m -> m.getMemberName().equals(memberName));
    }

    public void renameGroup(String oldGroupName, String newGroupName) {
        Group group = (Group) groups.getExistingGroup(oldGroupName);
        if (newGroupName.contains(" ")) {
            throw new BadRequestException("Le nom contient des espaces");
        }

        if (groupExists(newGroupName)) {
            throw new ConflictException("Le nom est déjà utilisé par un autre groupe");
        }

        if (group != null) {
            group.rename(newGroupName);
        } else {
            throw new NotFoundException("Le groupe " + oldGroupName + " n'existe pas");
        }
    }

    public ExpenseHistoryDTO getGroupExpenses(String groupName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);
        if (existingGroup == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }

        return existingGroup.generateGroupExpenses();
    }

    public ExpenseHistoryDTO getGroupExpensesFilteredByMemberName(String groupName, String memberName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);
        if (existingGroup == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }
        if (!existingGroup.memberNameExists(memberName)) {
            throw new NotFoundException("Le membre " + memberName + " n'existe pas dans ce groupe");
        }
        return existingGroup.generateGroupExpensesFilteredByMemberName(memberName);
    }
}
