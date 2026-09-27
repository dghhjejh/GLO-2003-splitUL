package ca.ulaval.glo2003.routes.member.infra;

import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceMongo;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistenceTest;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberPersistenceMongoTest extends MemberPersistenceTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    private Datastore datastore;

    @BeforeAll
    void setUp() {
        String mongoURL = mongoDBContainer.getConnectionString() + "/?uuidRepresentation=STANDARD";
        datastore = Morphia.createDatastore(MongoClients.create(mongoURL), "member-test");
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
