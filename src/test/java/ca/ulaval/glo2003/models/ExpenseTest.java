package ca.ulaval.glo2003.models;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo2003.routes.expense.logic.Expense;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ExpenseTest {

    private String DESCRIPTION = "Achat";
    private double AMOUNT = 50.0;
    private String PAID_BY = "junix";
    private String SPlIT_METHOD = "equally";
    private LocalDate PURCHASE_DATE = LocalDate.now();
    private Expense expense = new Expense(DESCRIPTION, AMOUNT, PURCHASE_DATE, PAID_BY, SPlIT_METHOD);

    @Test
    void givenExpenseDetails_whenCreatingExpense_thenExpenseHasCorrectValues() {
        assertNotNull(expense.getId());
        assertEquals(DESCRIPTION, expense.getDescription());
        assertEquals(AMOUNT, expense.getAmount());
        assertEquals(PURCHASE_DATE, expense.getPurchaseDate());
        assertEquals(PAID_BY, expense.getPaidBy());
        assertEquals(SPlIT_METHOD, expense.getSplit());
        assertEquals(PURCHASE_DATE, expense.getPurchaseDate());
    }
}
