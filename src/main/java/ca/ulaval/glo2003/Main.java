package ca.ulaval.glo2003;

import ca.ulaval.glo2003.routes.expense.api.ExpenseResource;
import ca.ulaval.glo2003.routes.expense.logic.ExpensePersistence;
import ca.ulaval.glo2003.routes.group.api.GroupResource;
import ca.ulaval.glo2003.routes.group.logic.GroupPersistence;
import ca.ulaval.glo2003.routes.health.HealthResource;
import ca.ulaval.glo2003.routes.member.api.MemberResource;
import ca.ulaval.glo2003.routes.member.logic.MemberPersistence;
import ca.ulaval.glo2003.shared.exceptions.mappers.*;
import ca.ulaval.glo2003.shared.infra.PersistenceFactory;
import io.sentry.Sentry;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {

    public static final String BASE_URI = "http://0.0.0.0:" + System.getenv("PORT") + "/";
    static final String SENTRY_DSN = System.getenv().getOrDefault("SENTRY_DSN", "");

    public static HttpServer startServer() {
        final PersistenceFactory persistenceFactory = PersistenceFactory.getInstance();

        final GroupPersistence groupPersistence = persistenceFactory.createGroupPersistence();
        final MemberPersistence memberPersistence = persistenceFactory.createMemberPersistence();
        final ExpensePersistence expensePersistence = persistenceFactory.createExpensePersistence();

        final ResourceConfig rc = new ResourceConfig();

        rc
            .register(new HealthResource())
            .register(new GroupResource(groupPersistence))
            .register(new MemberResource(memberPersistence, groupPersistence))
            .register(new ExpenseResource(expensePersistence, groupPersistence));

        rc
            .register(new BadRequestExceptionMapper())
            .register(new ConflictExceptionMapper())
            .register(new InvalidBodyExceptionMapper())
            .register(new ForbiddenExceptionMapper())
            .register(new NotFoundExceptionMapper())
            .register(new InvalidActionExceptionMapper())
            .register(new GlobalExceptionMapper());

        Sentry.init(options -> {
            options.setDsn(SENTRY_DSN);
            options.setEnableExternalConfiguration(true);
        });
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        startServer();
        System.out.printf("Jersey app started with endpoints available at %s%n", BASE_URI);
    }
}
