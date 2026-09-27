package ca.ulaval.glo2003.routes.expense.infra;

import ca.ulaval.glo2003.routes.expense.api.ExpenseRequest;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.group.infra.GroupMongo;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.UUID;

public class ExpensePersistenceMongo implements ExpensePersistence {

    private final Datastore datastore;

    public ExpensePersistenceMongo(Datastore datastore) {
        this.datastore = datastore;
    }

    public void verifyGroupAndMemberExist(String groupName, String memberName) {
        GroupMongo existingGroup = getExistingGroup(groupName);

        if (!existingGroup.memberNameExists(memberName)) throw new NotFoundException(
            " Le membre " + memberName + " n'existe pas dans ce groupe"
        );
    }

    public UUID addExpense(String groupName, ExpenseRequest request) {
        GroupMongo existingGroup = getExistingGroup(groupName);

        ExpenseMongo newExpense = new ExpenseMongo(
            request.description,
            request.amount,
            request.purchaseDate,
            request.paidBy,
            request.split
        );

        ArrayList<IMember> membersWithNewDebt = existingGroup.addExpense(newExpense);

        datastore.save(newExpense);
        for (IMember memberWithNewDebt : membersWithNewDebt) {
            datastore.save(memberWithNewDebt);
        }
        datastore.save(existingGroup);

        return newExpense.getId();
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
