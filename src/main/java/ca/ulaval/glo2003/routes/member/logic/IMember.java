package ca.ulaval.glo2003.routes.member.logic;

import java.util.Map;
import java.util.UUID;

public interface IMember {
    UUID retrieveId();
    String getMemberName();
    void setMemberName(String newMemberName);
    void addDebt(String paidByMemberName, double debtAmount);
    void settleDebt(String debtToPayToMemberName);
    Map<String, Double> getDebts();
}
