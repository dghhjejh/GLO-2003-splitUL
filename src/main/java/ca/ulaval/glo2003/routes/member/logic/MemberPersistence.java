package ca.ulaval.glo2003.routes.member.logic;

import java.util.ArrayList;

public interface MemberPersistence {
    void verifyMemberExistAlready(String groupName, String memberName);

    void addMember(String groupName, String memberName);

    ArrayList<IMember> getMembers(String groupName);

    void settleDebt(String groupName, String indebtedMemberName, String debtToPayToMemberName);
    void changeMemberName(String groupName, String currentName, String newName);
}
