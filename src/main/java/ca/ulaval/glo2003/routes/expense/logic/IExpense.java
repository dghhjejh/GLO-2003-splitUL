package ca.ulaval.glo2003.routes.expense.logic;

import java.time.LocalDate;
import java.util.UUID;

public interface IExpense {
    UUID getId();

    String getDescription();

    double getAmount();

    LocalDate getPurchaseDate();

    String getPaidBy();
    void setPaidBy(String newPaidBy);

    String getSplit();
}
