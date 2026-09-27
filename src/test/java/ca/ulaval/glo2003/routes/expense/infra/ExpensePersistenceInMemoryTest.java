package ca.ulaval.glo2003.routes.expense.infra;

import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistenceTest;
import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceInMemory;
import ca.ulaval.glo2003.routes.member.infra.MemberPersistenceInMemory;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;

class ExpensePersistenceInMemoryTest extends ExpensePersistenceTest {

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
