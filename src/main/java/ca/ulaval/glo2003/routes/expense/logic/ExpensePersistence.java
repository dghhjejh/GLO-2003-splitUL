package ca.ulaval.glo2003.routes.expense.logic;

import ca.ulaval.glo2003.routes.expense.api.ExpenseRequest;
import java.util.UUID;

public interface ExpensePersistence {
    void verifyGroupAndMemberExist(String groupName, String memberName);

    UUID addExpense(String groupName, ExpenseRequest request);
}
