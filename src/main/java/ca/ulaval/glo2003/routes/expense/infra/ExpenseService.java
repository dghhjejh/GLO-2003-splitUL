package ca.ulaval.glo2003.routes.expense.infra;

import static ca.ulaval.glo2003.shared.Utils.verifyPurchaseDate;

import ca.ulaval.glo2003.Main;
import ca.ulaval.glo2003.routes.expense.api.ExpenseRequest;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.shared.MemberAuthService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.net.URI;
import java.util.UUID;

public class ExpenseService {

    private final ExpensePersistence expensePersistence;
    private final MemberAuthService authService;

    public ExpenseService(ExpensePersistence expensePersistence, GroupPersistence groupPersistence) {
        this.expensePersistence = expensePersistence;
        this.authService = MemberAuthService.getInstance(groupPersistence);
    }

    public URI addExpenseToGroup(
        ContainerRequestContext requestContext,
        String groupName,
        ExpenseRequest expenseRequest
    ) {
        authService.validateAuthentication(requestContext, groupName);

        if (expenseRequest.amount < 0) throw new BadRequestException("Le montant est negatif");

        String purchaseDate = expenseRequest.purchaseDate;
        String paidBy = expenseRequest.paidBy;

        verifyPurchaseDate(purchaseDate);
        expensePersistence.verifyGroupAndMemberExist(groupName, paidBy);
        UUID expenseId = expensePersistence.addExpense(groupName, expenseRequest);

        return URI.create(Main.BASE_URI + "groups/" + groupName + "/expenses/" + expenseId);
    }
}
