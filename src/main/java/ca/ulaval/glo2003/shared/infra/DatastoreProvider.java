package ca.ulaval.glo2003.shared.infra;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import java.util.concurrent.TimeUnit;

public class DatastoreProvider {

    public static final String DATABASE_URI =
        System.getenv("MONGO_CLUSTER_URL") + "/?uuidRepresentation=STANDARD";
    public static final String DATABASE_NAME = System.getenv("MONGO_DATABASE");

    public Datastore provide() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(DATABASE_URI))
            .applyToClusterSettings(builder -> builder.serverSelectionTimeout(15, TimeUnit.SECONDS))
            .applyToConnectionPoolSettings(builder -> builder.maxConnectionIdleTime(30, TimeUnit.SECONDS))
            .build();
        MongoClient mongoClient = MongoClients.create(settings);

        return Morphia.createDatastore(mongoClient, DATABASE_NAME);
    }
}
