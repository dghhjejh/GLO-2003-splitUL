package ca.ulaval.glo2003.routes.expense.infra;

import java.time.LocalDate;

public class ExpenseDTO {

    public final String description;
    public final double amount;
    public final LocalDate purchaseDate;
    public final String paidBy;
    public final String split;

    public ExpenseDTO(
        String description,
        double amount,
        LocalDate purchaseDate,
        String paidBy,
        String split
    ) {
        this.description = description;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.paidBy = paidBy;
        this.split = split;
    }
}
