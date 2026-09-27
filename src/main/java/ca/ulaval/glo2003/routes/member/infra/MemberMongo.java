package ca.ulaval.glo2003.routes.member.infra;

import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.shared.Utils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity("members")
public class MemberMongo implements IMember {

    @Id
    private final UUID id = UUID.randomUUID();

    private String memberName;

    private Map<String, Double> debts;

    public MemberMongo() {}

    public MemberMongo(String memberName, HashMap<String, Double> debts) {
        this.memberName = memberName;
        this.debts = debts;
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
        if (this.debts != null) {
            Double alreadyExistingDebt = this.debts.get(paidByMemberName);

            if (alreadyExistingDebt != null) {
                double roundedNewDebt = Utils.roundAmount(alreadyExistingDebt + newDebtAmount);
                this.debts.put(paidByMemberName, roundedNewDebt);
            } else {
                this.debts.put(paidByMemberName, newDebtAmount);
            }
        } else {
            this.debts = new HashMap<>();
            this.debts.put(paidByMemberName, newDebtAmount);
        }
    }

    public void settleDebt(String debtToPayToMemberName) {
        this.debts.remove(debtToPayToMemberName);
    }

    public Map<String, Double> getDebts() {
        if (this.debts == null) return new HashMap<>();
        return this.debts;
    }
}
