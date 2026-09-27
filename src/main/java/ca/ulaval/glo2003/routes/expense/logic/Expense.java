package ca.ulaval.glo2003.routes.expense.logic;

import ca.ulaval.glo2003.shared.Utils;
import java.time.LocalDate;
import java.util.UUID;

public class Expense implements IExpense {

    private final UUID id = UUID.randomUUID();
    private final String description;
    private final double amount;
    private final LocalDate purchaseDate;
    private String paidBy;
    private final String split;

    public Expense(String description, double amount, LocalDate purchaseDate, String paidBy, String split) {
        this.description = description;
        this.amount = Utils.roundAmount(amount);
        this.purchaseDate = purchaseDate;
        this.paidBy = paidBy;
        this.split = split;
    }

    public UUID getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public double getAmount() {
        return this.amount;
    }

    public LocalDate getPurchaseDate() {
        return this.purchaseDate;
    }

    public String getPaidBy() {
        return this.paidBy;
    }

    public void setPaidBy(String newPaidBy) {
        this.paidBy = newPaidBy;
    }

    public String getSplit() {
        return this.split;
    }
}
