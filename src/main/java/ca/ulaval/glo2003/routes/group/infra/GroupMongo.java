package ca.ulaval.glo2003.routes.group.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseDTO;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import ca.ulaval.glo2003.routes.expense.infra.ExpenseMongo;
import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.routes.member.infra.MemberMongo;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.shared.Utils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity("groups")
public class GroupMongo implements IGroup {

    @Id
    private UUID id = UUID.randomUUID();

    private String name;

    @Reference
    private List<MemberMongo> members;

    @Reference
    private List<ExpenseMongo> expenses;

    public GroupMongo() {}

    public GroupMongo(String name, List<MemberMongo> members, List<ExpenseMongo> expenses) {
        this.name = name;
        this.members = members;
        this.expenses = expenses;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public UUID retrieveId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<IMember> getMembers() {
        if (this.members == null) return new ArrayList<>();

        return new ArrayList<>(this.members);
    }

    public ArrayList<IExpense> getExpenses() {
        if (this.expenses == null) return new ArrayList<>();

        return new ArrayList<>(this.expenses);
    }

    public void addMember(IMember newMember) {
        if (this.members == null) {
            this.members = new ArrayList<>();
            this.members.add((MemberMongo) newMember);
        } else {
            int index = this.members.indexOf((MemberMongo) newMember);

            if (index == -1) {
                this.members.add((MemberMongo) newMember);
            } else {
                this.members.set(index, (MemberMongo) newMember);
            }
        }
    }

    public ArrayList<IMember> addExpense(IExpense newExpense) {
        if (this.expenses == null) {
            this.expenses = new ArrayList<>();
            this.expenses.add((ExpenseMongo) newExpense);
        } else {
            int index = this.expenses.indexOf((ExpenseMongo) newExpense);

            if (index == -1) {
                this.expenses.add((ExpenseMongo) newExpense);
            } else {
                this.expenses.set(index, (ExpenseMongo) newExpense);
            }
        }

        return generateDebts(newExpense);
    }

    public boolean memberNameExists(String memberName) {
        if (this.members == null) return false;

        return members.stream().anyMatch(m -> m.getMemberName().equals(memberName));
    }

    public IMember settleDebt(String indebtedMemberName, String debtToPayToMemberName) {
        for (MemberMongo member : members) {
            if (member.getMemberName().equals(indebtedMemberName)) {
                member.settleDebt(debtToPayToMemberName);
                return member;
            }
        }

        return null;
    }

    public ExpenseHistoryDTO generateGroupExpenses() {
        if (this.expenses == null) return new ExpenseHistoryDTO(0, new ArrayList<>());

        double total = this.expenses.stream().mapToDouble(ExpenseMongo::getAmount).sum();

        this.expenses.sort((e1, e2) -> e2.getPurchaseDate().compareTo(e1.getPurchaseDate()));

        ArrayList<ExpenseDTO> expenseDTOs = new ArrayList<>();
        for (ExpenseMongo expense : this.expenses) {
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
        if (this.members == null) return false;

        for (MemberMongo member : this.members) {
            if (!member.getDebts().isEmpty()) {
                return true;
            }
        }

        return false;
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

    public ExpenseHistoryDTO generateGroupExpensesFilteredByMemberName(String memberName) {
        if (this.expenses == null) {
            return new ExpenseHistoryDTO(0, new ArrayList<>());
        }

        double total =
            this.expenses.stream()
                .filter(expense -> expense.getPaidBy().equals(memberName))
                .mapToDouble(ExpenseMongo::getAmount)
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
}
