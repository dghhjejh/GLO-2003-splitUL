package ca.ulaval.glo2003.routes.group.infra;

import ca.ulaval.glo2003.routes.expense.infra.ExpensePersistenceMongo;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistenceTest;
import ca.ulaval.glo2003.routes.member.infra.MemberPersistenceMongo;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupPersistenceMongoTest extends GroupPersistenceTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    private Datastore datastore;

    @BeforeAll
    void setUp() {
        String mongoURL = mongoDBContainer.getConnectionString() + "/?uuidRepresentation=STANDARD";
        datastore = Morphia.createDatastore(MongoClients.create(mongoURL), "group-test");
    }

    @Override
    protected ExpensePersistenceMongo createExpensePersistence() {
        return new ExpensePersistenceMongo(datastore);
    }

    @Override
    protected GroupPersistenceMongo createGroupPersistence() {
        return new GroupPersistenceMongo(datastore);
    }

    @Override
    protected MemberPersistence createMemberPersistence() {
        return new MemberPersistenceMongo(datastore);
    }
}
