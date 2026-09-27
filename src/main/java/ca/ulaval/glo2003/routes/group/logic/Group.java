package ca.ulaval.glo2003.routes.group.logic;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseDTO;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseMongo;
import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.shared.Utils;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Group implements IGroup {

    private String name;
    private final ArrayList<IMember> members;
    private final ArrayList<IExpense> expenses;

    public Group(String name, ArrayList<IMember> members, ArrayList<IExpense> expenses) {
        this.name = name;
        this.members = members;
        this.expenses = expenses;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<IMember> getMembers() {
        return this.members;
    }

    public ArrayList<IExpense> getExpenses() {
        return this.expenses;
    }

    public void addMember(IMember newMember) {
        this.members.add(newMember);
    }

    public ArrayList<IMember> addExpense(IExpense newExpense) {
        this.expenses.add(newExpense);
        return generateDebts(newExpense);
    }

    public boolean memberNameExists(String memberName) {
        if (this.members.isEmpty()) return false;

        return this.members.stream().anyMatch(m -> m.getMemberName().equals(memberName));
    }

    public IMember settleDebt(String indebtedMemberName, String debtToPayToMemberName) {
        for (IMember member : members) {
            if (member.getMemberName().equals(indebtedMemberName)) {
                member.settleDebt(debtToPayToMemberName);
                return member;
            }
        }

        return null;
    }

    public ExpenseHistoryDTO generateGroupExpenses() {
        double total = this.expenses.stream().mapToDouble(IExpense::getAmount).sum();

        this.expenses.sort((e1, e2) -> e2.getPurchaseDate().compareTo(e1.getPurchaseDate()));

        ArrayList<ExpenseDTO> expenseDTOs = new ArrayList<>();
        for (IExpense expense : this.expenses) {
            ExpenseDTO dto = new ExpenseDTO(
                expense.getDescription(),
                expense.getAmount(),
                expense.getPurchaseDate(),
                expense.getPaidBy(),
                expense.getSplit()
            );
            expenseDTOs.add(dto);
        }

        return new ExpenseHistoryDTO(total, expenseDTOs);
    }

    public boolean hasUnsettledDebts() {
        if (this.members.isEmpty()) return false;

        for (IMember member : this.members) {
            if (!member.getDebts().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public ExpenseHistoryDTO generateGroupExpensesFilteredByMemberName(String memberName) {
        if (this.expenses == null) {
            return new ExpenseHistoryDTO(0, new ArrayList<>());
        }

        double total =
            this.expenses.stream()
                .filter(expense -> expense.getPaidBy().equals(memberName))
                .mapToDouble(IExpense::getAmount)
                .sum();

        ArrayList<ExpenseDTO> expenseDTOS =
            this.expenses.stream()
                .filter(expense -> expense.getPaidBy().equals(memberName))
                .sorted((e1, e2) -> e2.getPurchaseDate().compareTo(e1.getPurchaseDate()))
                .map(expense ->
                    new ExpenseDTO(
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getPurchaseDate(),
                        expense.getPaidBy(),
                        expense.getSplit()
                    )
                )
                .collect(Collectors.toCollection(ArrayList::new));

        return new ExpenseHistoryDTO(total, expenseDTOS);
    }

    private ArrayList<IMember> generateDebts(IExpense expenseForDebtsGeneration) {
        ArrayList<IMember> membersWithNewDebt = new ArrayList<>();

        double dividedDebtAmount = Utils.roundAmount(
            expenseForDebtsGeneration.getAmount() / this.members.size()
        );
        String paidByMemberName = expenseForDebtsGeneration.getPaidBy();

        for (IMember member : this.members) {
            if (member.getMemberName().equals(paidByMemberName)) continue;
            member.addDebt(paidByMemberName, dividedDebtAmount);
            membersWithNewDebt.add(member);
        }

        return membersWithNewDebt;
    }

    public void rename(String newName) {
        this.name = newName;
    }
}
