package ca.ulaval.glo2003.routes.expense.logic;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo2003.routes.expense.api.ExpenseRequest;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class ExpensePersistenceTest {

    private String DESCRIPTION = "Achat";
    private double AMOUNT = 50.0;
    private String PAID_BY = "junix";
    private String SPLIT_METHOD = "equally";
    private LocalDate PURCHASE_DATE = LocalDate.now();
    private String GROUP_NAME = "kingston";
    private final String WRONG_GROUP_NAME = "wrongGroupName";
    private final String WRONG_GROUP = "wrongGroup";
    private final String WRONG_MEMBER = "wrongMember";

    private ExpenseRequest expenseRequest;
    private ExpensePersistence expensePersistence;

    protected abstract ExpensePersistence createExpensePersistence();

    private MemberPersistence memberPersistence;

    protected abstract MemberPersistence createMemberPersistence();

    private GroupPersistence groupPersistence;

    protected abstract GroupPersistence createGroupPersistence();

    @BeforeEach
    void setUp() {
        groupPersistence = createGroupPersistence();
        createGroup();
        expensePersistence = createExpensePersistence();
        memberPersistence = createMemberPersistence();
        memberPersistence.addMember(GROUP_NAME, PAID_BY);
        createExpenseRequest();
        createExpense();
    }

    void createGroup() {
        groupPersistence.addGroup(GROUP_NAME);
    }

    void createExpenseRequest() {
        expenseRequest = new ExpenseRequest();
        expenseRequest.description = DESCRIPTION;
        expenseRequest.amount = AMOUNT;
        expenseRequest.purchaseDate = PURCHASE_DATE.toString();
        expenseRequest.paidBy = PAID_BY;
        expenseRequest.split = SPLIT_METHOD;
    }

    void createExpense() {
        expensePersistence.addExpense(GROUP_NAME, expenseRequest);
    }

    @Test
    void givenWrongGroupAndMember_whenVerifyGroupAndMemberExist_thenThrowsNotFoundException() {
        assertThrows(Exception.class, () -> {
            expensePersistence.verifyGroupAndMemberExist(WRONG_GROUP, PAID_BY);
        });
    }

    @Test
    void givenGroupAndWrongMember_whenVerifyGroupAndMemberExist_thenThrowsNotFoundException() {
        assertThrows(Exception.class, () -> {
            expensePersistence.verifyGroupAndMemberExist(GROUP_NAME, WRONG_MEMBER);
        });
    }

    @Test
    void givenExpenseRequest_whenAddExpense_thenReturnsExpenseId() {
        setUp();
        var expenseID = expensePersistence.addExpense(GROUP_NAME, expenseRequest);
        assertNotNull(expenseID);
    }

    @Test
    void givenExpenseRequestWithWrongGroupName_whenAddExpense_thenThrowsException() {
        assertThrows(Exception.class, () -> {
            expensePersistence.addExpense(WRONG_GROUP_NAME, expenseRequest);
        });
    }
}
