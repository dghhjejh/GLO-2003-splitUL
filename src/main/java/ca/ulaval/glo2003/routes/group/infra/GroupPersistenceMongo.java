package ca.ulaval.glo2003.routes.group.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseMongo;
import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.group.logic.Group;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.routes.member.infra.MemberMongo;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import ca.ulaval.glo2003.shared.exceptions.InvalidActionException;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GroupPersistenceMongo implements GroupPersistence {

    private final Datastore datastore;

    public GroupPersistenceMongo(Datastore datastore) {
        this.datastore = datastore;
    }

    public ArrayList<IGroup> getGroups() {
        Query<GroupMongo> query = datastore.find(GroupMongo.class);
        List<GroupMongo> groups = query.iterator().toList();

        return new ArrayList<>(groups);
    }

    public IGroup getGroup(String groupName) {
        GroupMongo group = findExistingGroup(groupName);

        if (group != null) return group;

        throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
    }

    public void verifyGroupExistsAlready(String groupName) {
        GroupMongo group = findExistingGroup(groupName);

        if (group != null) throw new ConflictException("Le nom du groupe " + groupName + " existe deja");
    }

    public void addGroup(String newGroupName) {
        GroupMongo newGroup = new GroupMongo(newGroupName, new ArrayList<>(), new ArrayList<>());

        datastore.save(newGroup);
    }

    public void deleteGroup(String groupName) {
        GroupMongo groupToDelete = findExistingGroup(groupName);

        if (groupToDelete.hasUnsettledDebts()) {
            throw new InvalidActionException(
                "Le groupe ne peut pas être supprimé, certaines dettes ne sont pas réglées"
            );
        }

        ArrayList<IMember> members = groupToDelete.getMembers();
        ArrayList<IExpense> expenses = groupToDelete.getExpenses();

        for (IMember member : members) {
            Query<MemberMongo> query = datastore
                .find(MemberMongo.class)
                .filter(Filters.eq("_id", member.retrieveId()));

            query.delete();
        }

        for (IExpense expense : expenses) {
            Query<ExpenseMongo> query = datastore
                .find(ExpenseMongo.class)
                .filter(Filters.eq("_id", expense.getId()));

            query.delete();
        }

        Query<GroupMongo> query = datastore
            .find(GroupMongo.class)
            .filter(Filters.eq("_id", groupToDelete.retrieveId()));

        query.delete();
    }

    public ExpenseHistoryDTO getGroupExpenses(String groupName) {
        GroupMongo existingGroup = findExistingGroup(groupName);

        if (existingGroup == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }

        return existingGroup.generateGroupExpenses();
    }

    public ExpenseHistoryDTO getGroupExpensesFilteredByMemberName(String groupName, String memberName) {
        GroupMongo existingGroup = findExistingGroup(groupName);
        if (existingGroup == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }
        if (!existingGroup.memberNameExists(memberName)) {
            throw new NotFoundException("Le membre " + memberName + " n'existe pas dans ce groupe");
        }
        return existingGroup.generateGroupExpensesFilteredByMemberName(memberName);
    }

    private GroupMongo findExistingGroup(String groupName) {
        Query<GroupMongo> query = datastore.find(GroupMongo.class).filter(Filters.eq("name", groupName));

        return query.iterator().tryNext();
    }

    public boolean groupExists(String groupName) {
        return findExistingGroup(groupName) != null;
    }

    public boolean isMemberInGroup(String memberName, String groupName) {
        GroupMongo group = findExistingGroup(groupName);
        return (
            group != null &&
            group.getMembers().stream().anyMatch(member -> member.getMemberName().equals(memberName))
        );
    }

    public void renameGroup(String oldGroupName, String newGroupName) {
        GroupMongo group = findExistingGroup(oldGroupName);
        if (newGroupName.contains(" ")) {
            throw new BadRequestException("Le nom contient des espaces");
        }
        if (groupExists(newGroupName)) {
            throw new ConflictException("Le nom est déjà utilisé par un autre groupe");
        }
        if (group != null) {
            group.rename(newGroupName);
            datastore.save(group);
        } else {
            throw new NotFoundException("Le groupe " + oldGroupName + " n'existe pas");
        }
    }
}
