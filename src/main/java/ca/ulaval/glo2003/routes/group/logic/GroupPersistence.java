package ca.ulaval.glo2003.routes.group.logic;

import ca.ulaval.glo2003.routes.expense.infra.ExpenseHistoryDTO;
import java.util.ArrayList;

public interface GroupPersistence {
    ArrayList<IGroup> getGroups();
    IGroup getGroup(String groupName);
    void verifyGroupExistsAlready(String groupName);
    void addGroup(String newGroupName);
    void deleteGroup(String groupName);
    ExpenseHistoryDTO getGroupExpenses(String groupName);

    ExpenseHistoryDTO getGroupExpensesFilteredByMemberName(String groupName, String memberName);
    boolean groupExists(String groupName);
    boolean isMemberInGroup(String groupName, String memberName);
    void renameGroup(String oldGroupName, String newGroupName);
}
