package ca.ulaval.glo2003.shared.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpensePersistenceInMemory;
import ca.ulaval.glo2003.routes.expense.infra.ExpensePersistenceMongo;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceInMemory;
import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceMongo;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.member.infra.MemberPersistenceInMemory;
import ca.ulaval.glo2003.routes.member.infra.MemberPersistenceMongo;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;

public class PersistenceFactory {

    private static PersistenceFactory instance;
    private final DatastoreProvider datastoreProvider;
    private String persistenceType = "inmemory";

    private PersistenceFactory() {
        this.datastoreProvider = new DatastoreProvider();
        String persistenceSystemProperty = System.getProperty("persistence");
        if (
            persistenceSystemProperty != null &&
            (persistenceSystemProperty.equals("mongo") || persistenceSystemProperty.equals("inmemory"))
        ) this.persistenceType = persistenceSystemProperty;
    }

    public static PersistenceFactory getInstance() {
        if (instance == null) {
            instance = new PersistenceFactory();
        }
        return instance;
    }

    public GroupPersistence createGroupPersistence() {
        return switch (persistenceType) {
            case "inmemory" -> new GroupPersistenceInMemory();
            case "mongo" -> new GroupPersistenceMongo(datastoreProvider.provide());
            default -> null;
        };
    }

    public MemberPersistence createMemberPersistence() {
        return switch (persistenceType) {
            case "inmemory" -> new MemberPersistenceInMemory();
            case "mongo" -> new MemberPersistenceMongo(datastoreProvider.provide());
            default -> null;
        };
    }

    public ExpensePersistence createExpensePersistence() {
        return switch (persistenceType) {
            case "inmemory" -> new ExpensePersistenceInMemory();
            case "mongo" -> new ExpensePersistenceMongo(datastoreProvider.provide());
            default -> null;
        };
    }
}
