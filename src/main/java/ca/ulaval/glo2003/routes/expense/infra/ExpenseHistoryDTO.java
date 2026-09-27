package ca.ulaval.glo2003.routes.expense.infra;

import java.util.ArrayList;

public class ExpenseHistoryDTO {

    public final double total;
    public final ArrayList<ExpenseDTO> expenses;

    public ExpenseHistoryDTO(double total, ArrayList<ExpenseDTO> expenses) {
        this.total = total;
        this.expenses = expenses;
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<ExpenseDTO> getExpenses() {
        return expenses;
    }
}
