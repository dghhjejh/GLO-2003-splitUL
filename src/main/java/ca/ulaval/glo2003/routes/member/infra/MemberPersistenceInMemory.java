package ca.ulaval.glo2003.routes.member.infra;

import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.routes.member.logic.Member;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import ca.ulaval.glo2003.shared.infra.Groups;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Map;

public class MemberPersistenceInMemory implements MemberPersistence {

    private final Groups groups = Groups.getInstance();

    public void verifyMemberExistAlready(String groupName, String memberName) {
        IGroup existingGroup = getExistingGroup(groupName);

        if (existingGroup.memberNameExists(memberName)) throw new ConflictException(
            "Le membre " + memberName + " est deja dans le groupe"
        );
    }

    public void addMember(String groupName, String newMemberName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);
        IMember memberToAdd = new Member(newMemberName);
        existingGroup.addMember(memberToAdd);
    }

    public ArrayList<IMember> getMembers(String groupName) {
        IGroup existingGroup = getExistingGroup(groupName);

        if (existingGroup.getMembers().isEmpty()) {
            return new ArrayList<>();
        }

        return existingGroup.getMembers();
    }

    private IGroup getExistingGroup(String groupName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);

        if (existingGroup == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }

        return existingGroup;
    }

    public void settleDebt(String groupName, String indebtedMemberName, String debtToPayToMemberName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);
        if (existingGroup == null) {
            throw new NotFoundException("Le groupe " + groupName + " n'existe pas");
        }

        if (debtToPayToMemberName == null || !existingGroup.memberNameExists(debtToPayToMemberName)) {
            throw new NotFoundException(
                "Le membre " + debtToPayToMemberName + " n'existe pas dans ce groupe"
            );
        }

        if (indebtedMemberName == null || !existingGroup.memberNameExists(indebtedMemberName)) {
            throw new ForbiddenException("Vous n'etes pas membre du groupe");
        }

        existingGroup.settleDebt(indebtedMemberName, debtToPayToMemberName);
    }

    public void changeMemberName(String groupName, String currentName, String newName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);
        ArrayList<IMember> members = existingGroup.getMembers();
        ArrayList<IExpense> expenses = existingGroup.getExpenses();

        for (IMember member : members) {
            if (member.getMemberName().equals(currentName)) {
                member.setMemberName(newName);
            } else {
                Map<String, Double> debts = member.getDebts();
                double debtAmount = debts.get(currentName);
                debts.remove(currentName);
                debts.put(newName, debtAmount);
            }
        }

        for (IExpense expense : expenses) {
            if (expense.getPaidBy().equals(currentName)) {
                expense.setPaidBy(newName);
            }
        }
    }
}
