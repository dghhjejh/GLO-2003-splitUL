package ca.ulaval.glo2003.models;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo2003.routes.member.logic.Member;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MemberTest {

    private final String MEMBER_NAME = "member";
    private final String NEW_MEMBER_NAME = "renamedMember";
    private final String PAID_BY_MEMBER_NAME = "paidBy_Member";
    private final double DEBT_AMOUNT = 2.00;
    private final int NUMBER_OF_DEBTS = 1;

    @Test
    void givenMemberWithName_whenGetMemberName_thenReturnsCorrectName() {
        Member member = new Member(MEMBER_NAME);

        String member_name = member.getMemberName();

        assertEquals(member_name, MEMBER_NAME);
    }

    @Test
    void givenMember_whenNoDebts_thenGetDebtsReturnEmptyObject() {
        Member member = new Member(MEMBER_NAME);

        Map<String, Double> debts = member.getDebts();

        assertTrue(debts.isEmpty());
    }

    @Test
    void givenMember_whenOneDebtAdded_thenGetDebtsReturnsObjectWithDebt() {
        Member member = new Member(MEMBER_NAME);

        member.addDebt(PAID_BY_MEMBER_NAME, DEBT_AMOUNT);
        Map<String, Double> debts = member.getDebts();

        assertFalse(debts.isEmpty());
        assertEquals(NUMBER_OF_DEBTS, debts.size());
        assertEquals(DEBT_AMOUNT, debts.get(PAID_BY_MEMBER_NAME));
    }

    @Test
    void givenMember_whenRenamed_thenMemberNameChanged() {
        Member member = new Member(MEMBER_NAME);

        member.setMemberName(NEW_MEMBER_NAME);

        assertEquals(NEW_MEMBER_NAME, member.getMemberName());
    }
}
