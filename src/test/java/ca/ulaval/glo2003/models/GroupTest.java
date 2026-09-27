package ca.ulaval.glo2003.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.expense.logic.Expense;
import ca.ulaval.glo2003.routes.group.logic.Group;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.routes.member.logic.Member;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupTest {

    private final String GROUP_NAME = "kingston";
    private final Member MEMBER_1 = new Member("steeve");
    private final Member MEMBER_2 = new Member("bernice");
    private final Member MEMBER_3 = new Member("jeremy");
    private final double EXPENSE_1_AMOUNT = 50.0;
    private final double EXPENSE_2_AMOUNT = 30.0;
    private final double EXPENSE_1_DIVIDED_AMOUNT = 25.0;
    private final LocalDate NEWER_DATE = LocalDate.of(2024, 3, 15);
    private final LocalDate OLDER_DATE = LocalDate.of(2024, 3, 10);
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
    private final int NUMBER_OF_MEMBERS = 2;
    private final int NUMBER_OF_EXPENSES = 2;
    private final int NUMBER_OF_DEBTS = 1;
    private final int NEWER_DATE_INDEX = 0;
    private final int OLDER_DATE_INDEX = 1;
    private Group group;

    @BeforeEach
    void setup() {
        group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        group.addMember(MEMBER_1);
        group.addMember(MEMBER_2);
    }

    @Test
    void givenGroupWithName_whenGetName_thenReturnsCorrectName() {
        String groupName = group.getName();

        assertEquals(groupName, GROUP_NAME);
    }

    @Test
    void whenGetMembers_thenReturnsAllTheMembers() {
        ArrayList<IMember> members = group.getMembers();

        assertEquals(members.size(), NUMBER_OF_MEMBERS);
    }

    @Test
    void givenMember_whenAddMember_thenAddTheMember() {
        group.addMember(MEMBER_3);

        assertTrue(group.memberNameExists(MEMBER_3.getMemberName()));
    }

    @Test
    void givenMemberName_whenMemberWithThatNameExists_thenReturnsTrue() {
        boolean memberNameExists = group.memberNameExists(MEMBER_1.getMemberName());

        assertTrue(memberNameExists);
    }

    @Test
    void givenExpense_whenAddExpense_thenExpenseIsStoredInGroup() {
        group.addExpense(EXPENSE_1);

        assertTrue(group.getExpenses().contains(EXPENSE_1));
    }

    @Test
    void givenExpense_whenAddExpense_thenDebtsAreGenerated() {
        group.addExpense(EXPENSE_1);
        Map<String, Double> memberDebts = new HashMap<>();

        for (IMember member : group.getMembers()) {
            if (member.getMemberName().equals(MEMBER_2.getMemberName())) continue;
            memberDebts.putAll(member.getDebts());
        }

        assertFalse(memberDebts.isEmpty());
        assertEquals(NUMBER_OF_DEBTS, memberDebts.size());
        assertEquals(EXPENSE_1_DIVIDED_AMOUNT, memberDebts.get(MEMBER_2.getMemberName()));
    }

    @Test
    void givenGroup_whenGetExpenses_thenReturnsAllExpenses() {
        group.addExpense(EXPENSE_1);
        group.addExpense(EXPENSE_2);

        assertEquals(NUMBER_OF_EXPENSES, group.getExpenses().size());
    }

    @Test
    void givenGroupWithExpenses_whenGenerateGroupExpensesFilteredByMemberName_thenReturnsCorrectTotal() {
        group.addExpense(EXPENSE_1);
        group.addExpense(EXPENSE_2);

        ExpenseHistoryDTO filteredExpenses = group.generateGroupExpensesFilteredByMemberName("bernice");

        assertEquals(EXPENSE_1_AMOUNT + EXPENSE_2_AMOUNT, filteredExpenses.total);
    }

    @Test
    void givenGroupWithExpenses_whenGenerateGroupExpensesFilteredByMemberName_thenReturnsExpensesInDescendingChronologicalOrder() {
        group.addExpense(EXPENSE_1);
        group.addExpense(EXPENSE_2);

        ExpenseHistoryDTO filteredExpenses = group.generateGroupExpensesFilteredByMemberName("bernice");

        assertEquals(NUMBER_OF_EXPENSES, filteredExpenses.expenses.size());
        assertEquals(NEWER_DATE, filteredExpenses.expenses.get(NEWER_DATE_INDEX).purchaseDate);
        assertEquals(OLDER_DATE, filteredExpenses.expenses.get(OLDER_DATE_INDEX).purchaseDate);
    }

    @Test
    void givenGroupWithNoExpensesForMember_whenGenerateGroupExpensesFilteredByMemberName_thenReturnsEmptyList() {
        group.addExpense(EXPENSE_1);
        group.addExpense(EXPENSE_2);

        ExpenseHistoryDTO filteredExpenses = group.generateGroupExpensesFilteredByMemberName("steeve");

        assertTrue(filteredExpenses.expenses.isEmpty());
    }

    @Test
    void givenGroupWithValidNameForNewName_whenRename_thenNameIsUpdated() {
        String newName = "NewGroupName";
        group.rename(newName);

        assertEquals(newName, group.getName());
    }
}
