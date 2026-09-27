package ca.ulaval.glo2003.routes.member.infra;

import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceInMemory;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistenceTest;

class MemberPersistenceInMemoryTest extends MemberPersistenceTest {

    @Override
    protected MemberPersistence createMemberPersistence() {
        return new MemberPersistenceInMemory();
    }

    @Override
    protected GroupPersistenceInMemory createGroupPersistence() {
        return new GroupPersistenceInMemory();
    }
}
