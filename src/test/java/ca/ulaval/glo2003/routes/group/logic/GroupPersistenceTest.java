package ca.ulaval.glo2003.routes.group.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.glo2003.routes.expense.api.ExpenseRequest;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.expense.logic.Expense;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.member.logic.Member;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class GroupPersistenceTest {

    private final String GROUP_NAME = "Kingston";
    private final String GROUP_NAME_1 = "Moncton";
    private final Member MEMBER_1 = new Member("steeve");
    private final Member MEMBER_2 = new Member("bernice");
    private final String NON_EXISTING_MEMBER_NAME = "nonExistingMember";
    private final String NON_EXISTING_GROUP_NAME = "nonExistingGroup";
    private final String WRONG_MEMBER_NAME = "wrongName";
    private final String WRONG_GROUP_NAME = "wrongGroup";
    private final double EXPENSE_1_AMOUNT = 50.0;
    private final double EXPENSE_2_AMOUNT = 30.0;
    private final int NUMBER_OF_EXPENSES = 2;
    private final int NUMBER_ONE = 1;
    private final LocalDate NEWER_DATE = LocalDate.of(2024, 12, 15);
    private final LocalDate OLDER_DATE = LocalDate.of(2023, 3, 10);
    private final int NEWER_DATE_INDEX = 0;
    private final int OLDER_DATE_INDEX = 1;
    private final Expense EXPENSE_1 = new Expense(
        "Soupé",
        EXPENSE_1_AMOUNT,
        OLDER_DATE,
        "bernice",
        "equally"
    );
    private final Expense EXPENSE_2 = new Expense(
        "Transport",
        EXPENSE_2_AMOUNT,
        NEWER_DATE,
        "bernice",
        "equally"
    );

    private MemberPersistence memberPersistence;

    protected abstract MemberPersistence createMemberPersistence();

    private GroupPersistence groupPersistence;

    protected abstract GroupPersistence createGroupPersistence();

    private ExpenseRequest expenseRequest;
    private ExpensePersistence expensePersistence;

    protected abstract ExpensePersistence createExpensePersistence();

    @BeforeEach
    void setup() {
        groupPersistence = createGroupPersistence();
    }

    void createOneGroup() {
        groupPersistence.addGroup(GROUP_NAME);
    }

    void createTwoGroups() {
        groupPersistence.addGroup(GROUP_NAME);
        groupPersistence.addGroup(GROUP_NAME_1);
    }

    private ExpenseRequest createExpenseRequest(Expense expense) {
        ExpenseRequest request = new ExpenseRequest();
        request.description = expense.getDescription();
        request.amount = expense.getAmount();
        request.purchaseDate = expense.getPurchaseDate().toString();
        request.paidBy = expense.getPaidBy();
        request.split = expense.getSplit();
        return request;
    }

    @Test
    void givenGroupWithName_whenGetName_thenReturnsCorrectName() {
        createOneGroup();
        var group = groupPersistence.getGroup(GROUP_NAME);
        String groupName = group.getName();
        assertEquals(groupName, GROUP_NAME);
    }

    @Test
    void givenGroupWithWrongName_whenGetName_thenThrowsNotFoundException() {
        createOneGroup();
        assertThrows(NotFoundException.class, () -> {
            groupPersistence.getGroup(WRONG_MEMBER_NAME);
        });
    }

    @Test
    void givenTwoGroups_whenGetGroups_thenReturnsAllGroups() {
        createTwoGroups();
        assertDoesNotThrow(groupPersistence.getGroup(GROUP_NAME)::getName);
        assertDoesNotThrow(groupPersistence.getGroup(GROUP_NAME_1)::getName);
    }

    @Test
    void givenWrongGroup_whenGetGroup_thenThrowsNotFoundException() {
        createOneGroup();
        assertThrows(NotFoundException.class, () -> {
            groupPersistence.getGroup(WRONG_GROUP_NAME);
        });
    }

    @Test
    void givenGroup_whenGetGroup_thenReturnsCorrectGroup() {
        createOneGroup();
        var group = groupPersistence.getGroup(GROUP_NAME);
        assertEquals(GROUP_NAME, group.getName());
    }

    @Test
    void givenGroup_whenVerifyGroupExistsAlready_thenThrowsConflictException() {
        createOneGroup();
        assertThrows(ConflictException.class, () -> {
            groupPersistence.verifyGroupExistsAlready(GROUP_NAME);
        });
    }

    @Test
    void givenWrongGroup_whenVerifyGroupExistsAlready_thenDoesNotThrow() {
        createOneGroup();
        assertDoesNotThrow(() -> {
            groupPersistence.verifyGroupExistsAlready(WRONG_GROUP_NAME);
        });
    }

    @Test
    void givenGroup_whenAddGroup_thenGroupIsAdded() {
        groupPersistence.addGroup(GROUP_NAME_1);
        assertDoesNotThrow(groupPersistence.getGroup(GROUP_NAME_1)::getName);
    }

    @Test
    void givenExistingGroup_whenAddGroup_thenOverridesWithNoThrow() {
        createOneGroup();
        assertDoesNotThrow(() -> {
            groupPersistence.addGroup(GROUP_NAME);
        });
    }

    @Test
    void givenGroup_whenDeleteGroup_thenGroupIsDeleted() {
        createOneGroup();
        var group = groupPersistence.getGroup(GROUP_NAME);
        var groupInitialCount = groupPersistence.getGroups().size();
        groupPersistence.deleteGroup(group.getName());
        var groupFinalCount = groupPersistence.getGroups().size();
        assertEquals(groupInitialCount - NUMBER_ONE, groupFinalCount);
    }

    @Test
    void givenNonExistingGroup_whenDeleteGroup_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            var group = groupPersistence.getGroup(NON_EXISTING_GROUP_NAME);
            groupPersistence.deleteGroup(group.getName());
        });
    }

    @Test
    void givenGroupWithExpensesForMember_whenGetGroupExpensesFilteredByMemberName_thenReturnsCorrectTotal() {
        createOneGroup();
        memberPersistence = createMemberPersistence();
        memberPersistence.addMember(GROUP_NAME, MEMBER_2.getMemberName());
        expensePersistence = createExpensePersistence();
        expensePersistence.addExpense(GROUP_NAME, createExpenseRequest(EXPENSE_1));
        expensePersistence.addExpense(GROUP_NAME, createExpenseRequest(EXPENSE_2));

        ExpenseHistoryDTO filteredExpenses = groupPersistence.getGroupExpensesFilteredByMemberName(
            GROUP_NAME,
            "bernice"
        );

        assertEquals(EXPENSE_1_AMOUNT + EXPENSE_2_AMOUNT, filteredExpenses.getTotal());
    }

    @Test
    void givenGroupWithExpensesForMember_whenGetGroupExpensesFilteredByMemberName_thenReturnsExpensesInChronologicalOrder() {
        ExpenseHistoryDTO filteredExpenses = groupPersistence.getGroupExpensesFilteredByMemberName(
            GROUP_NAME,
            "bernice"
        );
        var expenses = filteredExpenses.getExpenses();

        assertEquals(NUMBER_OF_EXPENSES, expenses.size());
        assertEquals(NEWER_DATE, expenses.get(NEWER_DATE_INDEX).purchaseDate);
        assertEquals(OLDER_DATE, expenses.get(OLDER_DATE_INDEX).purchaseDate);
    }

    @Test
    void givenGroupWithNoExpensesForMember_whenGetGroupExpensesFilteredByMemberName_thenReturnsEmptyHistory() {
        createOneGroup();
        memberPersistence = createMemberPersistence();
        memberPersistence.addMember(GROUP_NAME, MEMBER_1.getMemberName());

        ExpenseHistoryDTO filteredExpenses = groupPersistence.getGroupExpensesFilteredByMemberName(
            GROUP_NAME,
            "steeve"
        );

        assertTrue(filteredExpenses.getExpenses().isEmpty());
    }

    @Test
    void givenGroupWithExpensesForNonExistingMember_whenGetGroupExpensesFilteredByMemberName_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            groupPersistence.getGroupExpensesFilteredByMemberName(GROUP_NAME, NON_EXISTING_MEMBER_NAME);
        });
    }

    @Test
    void givenNonExistingGroup_whenGetGroupExpensesFilteredByMemberName_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            groupPersistence.getGroupExpensesFilteredByMemberName(
                NON_EXISTING_GROUP_NAME,
                MEMBER_1.getMemberName()
            );
        });
    }

    @Test
    void givenExistingGroup_whenRenameGroup_thenGroupIsRenamed() {
        createOneGroup();

        String newName = "newGroupTest";
        groupPersistence.renameGroup(GROUP_NAME, newName);

        var renamedGroup = groupPersistence.getGroup(newName);
        assertEquals(newName, renamedGroup.getName());
    }

    @Test
    void givenGroup_whenRenameGroupWithSpacesInName_thenThrowsBadRequestException() {
        createOneGroup();
        String newNameWithSpaces = "new group";

        assertThrows(BadRequestException.class, () -> {
            groupPersistence.renameGroup(GROUP_NAME, newNameWithSpaces);
        });
    }

    @Test
    void givenNonExistingGroup_whenRenameGroup_thenThrowsNotFoundException() {
        String nonExistingGroup = "AlcideBarbeauGroup";
        String newName = "newRitaChabotGroup";

        assertThrows(NotFoundException.class, () -> {
            groupPersistence.renameGroup(nonExistingGroup, newName);
        });
    }

    @Test
    void givenGroupWithExistingTargetName_whenRenameGroup_thenThrowsConflictException() {
        createTwoGroups();
        assertThrows(ConflictException.class, () -> {
            groupPersistence.renameGroup(GROUP_NAME, GROUP_NAME_1);
        });
    }
}
