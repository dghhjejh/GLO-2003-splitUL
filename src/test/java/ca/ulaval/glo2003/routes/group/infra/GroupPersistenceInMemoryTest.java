package ca.ulaval.glo2003.routes.group.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpensePersistenceInMemory;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistenceTest;
import ca.ulaval.glo2003.routes.member.infra.MemberPersistenceInMemory;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;

class GroupPersistenceInMemoryTest extends GroupPersistenceTest {

    @Override
    protected ExpensePersistenceInMemory createExpensePersistence() {
        return new ExpensePersistenceInMemory();
    }

    @Override
    protected GroupPersistenceInMemory createGroupPersistence() {
        return new GroupPersistenceInMemory();
    }

    @Override
    protected MemberPersistence createMemberPersistence() {
        return new MemberPersistenceInMemory();
    }
}
