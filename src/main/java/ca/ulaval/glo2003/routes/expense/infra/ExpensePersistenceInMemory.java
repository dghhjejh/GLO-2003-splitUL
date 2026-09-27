package ca.ulaval.glo2003.routes.expense.infra;

import ca.ulaval.glo2003.routes.expense.api.ExpenseRequest;
import ca.ulaval.glo2003.routes.expense.logic.Expense;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.shared.infra.Groups;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ExpensePersistenceInMemory implements ExpensePersistence {

    private final Groups groups = Groups.getInstance();

    public void verifyGroupAndMemberExist(String groupName, String memberName) {
        IGroup existingGroup = groups.getExistingGroup(groupName);

        if (existingGroup == null) throw new NotFoundException(" Le groupe " + groupName + " n'existe pas");

        if (!existingGroup.memberNameExists((memberName))) throw new NotFoundException(
            " Le membre " + memberName + " n'existe pas dans ce groupe"
        );
    }

    public UUID addExpense(String groupName, ExpenseRequest request) {
        IGroup existingGroup = groups.getExistingGroup(groupName);

        Expense newExpense = new Expense(
            request.description,
            request.amount,
            LocalDate.parse(request.purchaseDate, DateTimeFormatter.ISO_LOCAL_DATE),
            request.paidBy,
            request.split
        );
        existingGroup.addExpense(newExpense);

        return newExpense.getId();
    }
}
