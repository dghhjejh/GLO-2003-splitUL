package ca.ulaval.glo2003.routes.group.logic;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import java.util.ArrayList;

public interface IGroup {
    String getName();

    ArrayList<IMember> getMembers();

    void addMember(IMember newMember);

    ArrayList<IExpense> getExpenses();

    ArrayList<IMember> addExpense(IExpense newExpense);

    boolean memberNameExists(String memberName);

    IMember settleDebt(String indebtedMemberName, String debtToPayToMemberName);

    ExpenseHistoryDTO generateGroupExpenses();

    boolean hasUnsettledDebts();

    ExpenseHistoryDTO generateGroupExpensesFilteredByMemberName(String memberName);
}
