package ca.ulaval.glo2003.routes.member.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseMongo;
import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.group.infra.GroupMongo;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberPersistenceMongo implements MemberPersistence {

    private final Datastore datastore;

    public MemberPersistenceMongo(Datastore datastore) {
        this.datastore = datastore;
    }

    public void verifyMemberExistAlready(String groupName, String memberName) {
        GroupMongo existingGroup = getExistingGroup(groupName);

        if (existingGroup.memberNameExists(memberName)) throw new ConflictException(
            "Le membre " + memberName + " est deja dans le groupe"
        );
    }

    public void addMember(String groupName, String newMemberName) {
        GroupMongo existingGroup = getExistingGroup(groupName);
        MemberMongo memberToAdd = new MemberMongo(newMemberName, new HashMap<>());

        existingGroup.addMember(memberToAdd);

        datastore.save(memberToAdd);
        datastore.save(existingGroup);
    }

    public ArrayList<IMember> getMembers(String groupName) {
        GroupMongo existingGroup = getExistingGroup(groupName);

        return existingGroup.getMembers();
    }

    public void settleDebt(String groupName, String indebtedMemberName, String debtToPayToMemberName) {
        GroupMongo existingGroup = getExistingGroup(groupName);

        if (debtToPayToMemberName == null || !existingGroup.memberNameExists(debtToPayToMemberName)) {
            throw new NotFoundException(
                "Le membre " + debtToPayToMemberName + " n'existe pas dans ce groupe"
            );
        }

        if (indebtedMemberName == null || !existingGroup.memberNameExists(indebtedMemberName)) {
            throw new ForbiddenException("Vous n'etes pas membre du groupe");
        }

        MemberMongo member = (MemberMongo) existingGroup.settleDebt(
            indebtedMemberName,
            debtToPayToMemberName
        );
        datastore.save(member);
        datastore.save(existingGroup);
    }

    public void changeMemberName(String groupName, String currentName, String newName) {
        GroupMongo existingGroup = getExistingGroup(groupName);
        ArrayList<IMember> members = existingGroup.getMembers();
        ArrayList<IExpense> expenses = existingGroup.getExpenses();

        for (IMember member : members) {
            if (member.getMemberName().equals(currentName)) {
                member.setMemberName(newName);
                datastore.save((MemberMongo) member);
            } else {
                Map<String, Double> debts = member.getDebts();
                double debtAmount = debts.get(currentName);
                debts.remove(currentName);
                debts.put(newName, debtAmount);
                datastore.save((MemberMongo) member);
            }
        }

        for (IExpense expense : expenses) {
            if (expense.getPaidBy().equals(currentName)) {
                expense.setPaidBy(newName);
                datastore.save((ExpenseMongo) expense);
            }
        }

        datastore.save(existingGroup);
    }

    private GroupMongo getExistingGroup(String groupName) {
        Query<GroupMongo> query = datastore.find(GroupMongo.class).filter(Filters.eq("name", groupName));
        GroupMongo group = query.iterator().tryNext();

        if (group == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }

        return group;
    }
}
