package ca.ulaval.glo2003.routes.member.logic;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.shared.exceptions.ConflictException;
import java.util.ArrayList;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class MemberPersistenceTest {

    private final String MEMBER_NAME = "member";
    private final String NEW_MEMBER_NAME = "memberRenamed";
    private final String MEMBER_NAME_2 = "member2";
    private final String GROUP_NAME = "groupName";
    private final int NUMBER_ONE = 1;
    private final int NUMBER_TWO = 2;

    private MemberPersistence memberPersistence;

    protected abstract MemberPersistence createMemberPersistence();

    private GroupPersistence groupPersistence;

    protected abstract GroupPersistence createGroupPersistence();

    @BeforeEach
    void setUp() {
        groupPersistence = createGroupPersistence();
        memberPersistence = createMemberPersistence();
    }

    void createGroup() {
        groupPersistence.addGroup(GROUP_NAME);
    }

    void createOneMember() {
        memberPersistence.addMember(GROUP_NAME, MEMBER_NAME);
    }

    void createTwoMembers() {
        memberPersistence.addMember(GROUP_NAME, MEMBER_NAME);
        memberPersistence.addMember(GROUP_NAME, MEMBER_NAME_2);
    }

    @Test
    void givenGroupWithMember_whenVerifyMemberExistAlready_thenThrowsConflictException() {
        createGroup();
        createOneMember();
        assertThrows(ConflictException.class, () -> {
            memberPersistence.verifyMemberExistAlready(GROUP_NAME, MEMBER_NAME);
        });
    }

    @Test
    void givenGroupWithMember_whenVerifyMemberExistAlreadyWithNonExistingMember_thenDoesNotThrowException() {
        setUp();
        createGroup();
        createOneMember();
        assertDoesNotThrow(() -> {
            memberPersistence.verifyMemberExistAlready(GROUP_NAME, "nonExistingMember");
        });
    }

    @Test
    void givenGroup_whenAddMember_thenMemberIsAdded() {
        createGroup();
        var membersInitialSize = memberPersistence.getMembers(GROUP_NAME).size();
        createOneMember();
        var membersFinalSize = memberPersistence.getMembers(GROUP_NAME).size();

        assertEquals(membersFinalSize, membersInitialSize + NUMBER_ONE);
    }

    @Test
    void givenGroupWithTwoMembers_whenGetMembers_thenReturnsAllMembers() {
        createGroup();
        var membersInitialSize = memberPersistence.getMembers(GROUP_NAME).size();
        createTwoMembers();
        var membersFinalSize = memberPersistence.getMembers(GROUP_NAME).size();
        assertEquals(membersFinalSize, membersInitialSize + NUMBER_TWO);
    }

    @Test
    void givenGroupWithNoMembers_whenGetMembers_thenReturnsNoMembers() {
        String groupName = "RandomGroupdsfgjjhsdbfjhdsf";
        groupPersistence.addGroup(groupName);
        var members = memberPersistence.getMembers(groupName);
        assertTrue(members.isEmpty());
    }

    @Test
    void givenGroupWithOneMember_whenRenameMember_thenChangesThatMemberName() {
        createGroup();
        createOneMember();

        memberPersistence.changeMemberName(GROUP_NAME, MEMBER_NAME, NEW_MEMBER_NAME);
        ArrayList<IMember> members = memberPersistence.getMembers(GROUP_NAME);
        boolean memberRenameSuccess = members
            .stream()
            .anyMatch(m -> m.getMemberName().equals(NEW_MEMBER_NAME));

        assertTrue(memberRenameSuccess);
    }
}
