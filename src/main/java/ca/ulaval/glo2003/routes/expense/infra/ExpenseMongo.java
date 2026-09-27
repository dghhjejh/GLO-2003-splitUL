package ca.ulaval.glo2003.routes.expense.infra;

import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity("expenses")
public class ExpenseMongo implements IExpense {

    @Id
    private final UUID id = UUID.randomUUID();

    private String description;
    private double amount;
    private String purchaseDate;
    private String paidBy;
    private String split;

    public ExpenseMongo(String description, double amount, String purchaseDate, String paidBy, String split) {
        this.description = description;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.paidBy = paidBy;
        this.split = split;
    }

    public ExpenseMongo() {}

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
        return LocalDate.parse(this.purchaseDate, DateTimeFormatter.ISO_LOCAL_DATE);
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
