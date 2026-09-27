package ca.ulaval.glo2003.routes.member.logic;

import ca.ulaval.glo2003.shared.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Member implements IMember {

    private final UUID id = UUID.randomUUID();
    private String memberName;
    private final Map<String, Double> debts = new HashMap<>();

    public Member(String name) {
        this.memberName = name;
    }

    public UUID retrieveId() {
        return this.id;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public void setMemberName(String newMemberName) {
        this.memberName = newMemberName;
    }

    public void addDebt(String paidByMemberName, double newDebtAmount) {
        Double alreadyExistingDebt = debts.get(paidByMemberName);

        if (alreadyExistingDebt != null) {
            double roundedNewDebt = Utils.roundAmount(alreadyExistingDebt + newDebtAmount);
            debts.put(paidByMemberName, roundedNewDebt);
        } else {
            debts.put(paidByMemberName, newDebtAmount);
        }
    }

    public void settleDebt(String debtToPayToMemberName) {
        debts.remove(debtToPayToMemberName);
    }

    public Map<String, Double> getDebts() {
        return this.debts;
    }
}
