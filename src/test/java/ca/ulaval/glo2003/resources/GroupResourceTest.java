package ca.ulaval.glo2003.resources;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo2003.routes.expense.logic.Expense;
import ca.ulaval.glo2003.routes.expense.logic.IExpense;
import ca.ulaval.glo2003.routes.group.api.GroupResource;
import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceInMemory;
import ca.ulaval.glo2003.routes.group.logic.Group;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.routes.member.logic.IMember;
import ca.ulaval.glo2003.routes.member.logic.Member;
import ca.ulaval.glo2003.shared.infra.Groups;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupResourceTest extends JerseyTest {

    private static final String GROUP_NAME = "Fantastic";
    private static final String MEMBER_NAME = "Fantastico";
    private final String MEMBER_HTTP_HEADER = "Member";
    private final String VALID_GROUP_POST_JSON = "{\"name\":\"Fantastic\"}";
    private final String INVALID_GROUP_POST_JSON = "{\"name\":\"Fan tastic\"}";
    private final String LOCATION_HEADER_KEY_NAME = "Location";
    private final Expense EXPENSE_1 = new Expense(
        "Épicerie",
        50.0,
        LocalDate.of(2024, 3, 15),
        MEMBER_NAME,
        "equally"
    );
    private final Expense EXPENSE_2 = new Expense(
        "Restaurant",
        30.0,
        LocalDate.of(2024, 3, 10),
        MEMBER_NAME,
        "equally"
    );
    private final String EXPECTED_TOTAL = "\"total\":80.0";
    private final String ZERO_TOTAL = "\"total\":0.0";
    private final String EMPTY_EXPENSES = "\"expenses\":[]";
    private final String NEWER_DATE = "\"2024-03-15\"";
    private final String OLDER_DATE = "\"2024-03-10\"";
    private final String LOCATION_HEADER_VALUE = "http://0.0.0.0:null/groups/Fantastic";
    private final int EXPECTED_EMPTY_GROUPS_SIZE = 0;
    private final int EXPECTED_NON_EMPTY_GROUPS_SIZE = 1;
    private final Groups groups = Groups.getInstance();

    private IExpense createExpense(String description, double amount, LocalDate date, String paidBy) {
        return new Expense(description, amount, date, paidBy, "equally");
    }

    @Override
    protected Application configure() {
        return new ResourceConfig().register(new GroupResource(new GroupPersistenceInMemory()));
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        groups.getGroups().clear();
    }

    @Test
    void givenNoGroups_whenGetGroups_thenReturns200AndEmptyArray() {
        Response response = target("/groups").request().get();
        ArrayList<IGroup> groups = response.readEntity(ArrayList.class);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(groups.isEmpty());
        assertEquals(EXPECTED_EMPTY_GROUPS_SIZE, groups.size());
    }

    @Test
    void givenOneGroup_whenGetGroups_thenReturns200AndNotEmptyArray() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups").request().get();
        ArrayList<IGroup> groups = response.readEntity(ArrayList.class);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertFalse(groups.isEmpty());
        assertEquals(EXPECTED_NON_EMPTY_GROUPS_SIZE, groups.size());
    }

    @Test
    void givenExistingGroup_whenGetGroupByName_thenReturns200AndGroup() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups/" + GROUP_NAME).request().get();
        String json = response.readEntity(String.class);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(json.contains(GROUP_NAME));
    }

    @Test
    void givenNonExistingGroup_whenGetGroupByName_thenReturns404() {
        Response response = target("/groups/" + GROUP_NAME).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void givenValidNonExistingGroup_whenPostGroup_thenReturns201WithLocationHeader() {
        Response response = target("/groups/").request().post(Entity.json(VALID_GROUP_POST_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(LOCATION_HEADER_VALUE, response.getHeaders().get(LOCATION_HEADER_KEY_NAME).getFirst());
    }

    @Test
    void givenInvalidNonExistingGroup_whenPostGroup_thenReturns400() {
        Response response = target("/groups/").request().post(Entity.json(INVALID_GROUP_POST_JSON));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void givenAlreadyExistingGroup_whenPostGroup_thenReturns409() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups/").request().post(Entity.json(VALID_GROUP_POST_JSON));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void givenExistingGroupWithUser_whenDeleteGroupByName_thenReturns204() {
        IMember member = new Member(MEMBER_NAME);
        ArrayList<IMember> members = new ArrayList<>();
        Group group;

        members.add(member);
        group = new Group(GROUP_NAME, members, new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups/" + GROUP_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void givenNonExistingGroup_whenDeleteGroupByName_thenReturns404() {
        Response response = target("/groups/" + GROUP_NAME).request().delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void givenGroupWithExpenses_whenGetGroupExpensesFilteredByMemberName_thenReturns200() {
        IMember member = new Member(MEMBER_NAME);
        ArrayList<IMember> members = new ArrayList<>();
        members.add(member);

        ArrayList<IExpense> expenses = new ArrayList<>();
        expenses.add(
            createExpense(
                EXPENSE_1.getDescription(),
                EXPENSE_1.getAmount(),
                EXPENSE_1.getPurchaseDate(),
                MEMBER_NAME
            )
        );
        expenses.add(
            createExpense(
                EXPENSE_2.getDescription(),
                EXPENSE_2.getAmount(),
                EXPENSE_2.getPurchaseDate(),
                MEMBER_NAME
            )
        );

        Group group = new Group(GROUP_NAME, members, expenses);
        groups.getGroups().add(group);

        Response response = target("/groups/" + GROUP_NAME + "/expenses/" + MEMBER_NAME)
            .queryParam("memberName", MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String json = response.readEntity(String.class);
        assertTrue(json.contains(EXPECTED_TOTAL));
        assertTrue(json.contains(NEWER_DATE));
        assertTrue(json.contains(OLDER_DATE));
    }

    @Test
    void givenGroupWithNoExpenses_whenGetGroupExpensesFilteredByMemberName_thenReturns200AndEmptyList() {
        IMember member = new Member(MEMBER_NAME);
        ArrayList<IMember> members = new ArrayList<>();
        members.add(member);

        Group group = new Group(GROUP_NAME, members, new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups/" + GROUP_NAME + "/expenses/" + MEMBER_NAME)
            .queryParam("memberName", MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String json = response.readEntity(String.class);
        assertTrue(json.contains(EMPTY_EXPENSES));
        assertTrue(json.contains(ZERO_TOTAL));
    }

    @Test
    void givenNonExistingMember_whenGetGroupExpensesFilteredByMemberName_thenReturns404() {
        IMember authenticatedMember = new Member(MEMBER_NAME);
        ArrayList<IMember> members = new ArrayList<>();
        members.add(authenticatedMember);

        Group group = new Group(GROUP_NAME, members, new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups/" + GROUP_NAME + "/expenses/" + "membreInexistant")
            .queryParam("memberName", "membreInexistant")
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void givenNonExistingGroup_whenGetGroupExpensesFilteredByMemberName_thenReturns404() {
        IMember authenticatedMember = new Member(MEMBER_NAME);
        ArrayList<IMember> members = new ArrayList<>();
        members.add(authenticatedMember);

        Response response = target("/groups/" + GROUP_NAME + "/expenses/" + MEMBER_NAME)
            .queryParam("memberName", MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void givenUnauthorizedMember_whenGetGroupExpensesFilteredByMemberName_thenReturns403() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group);

        Response response = target("/groups/" + GROUP_NAME + "/expenses/" + MEMBER_NAME)
            .queryParam("memberName", MEMBER_NAME)
            .request()
            .get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void givenValidGroupAndMember_whenRenameGroup_thenReturns200AndNewLocation() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        group.addMember(new Member(MEMBER_NAME));
        groups.getGroups().add(group);

        String newName = "newGrpoupName";
        String body = "{\"newName\":\"" + newName + "\"}";

        Response response = target("/groups/" + GROUP_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getHeaderString(LOCATION_HEADER_KEY_NAME).contains(newName));
    }

    @Test
    void givenGroup_whenRenameByNonMember_thenReturns403() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group);

        String newName = "newGroupName";
        String body = "{\"newName\":\"" + newName + "\"}";

        Response response = target("/groups/" + GROUP_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void givenNonExistingGroup_whenRename_thenReturns404() {
        String body = "{\"newName\":\"WrongGroup\"}";

        Response response = target("/groups/" + GROUP_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void givenGroup_whenRenameWithNameWithSpaces_thenReturns400() {
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        group.addMember(new Member(MEMBER_NAME));
        groups.getGroups().add(group);

        String body = "{\"newName\":\"Bad Name\"}";

        Response response = target("/groups/" + GROUP_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void givenGroupWithExistingTargetName_whenRename_thenReturns409() {
        Group group1 = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        group1.addMember(new Member(MEMBER_NAME));

        Group group2 = new Group("ExistingName", new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group1);
        groups.getGroups().add(group2);

        String body = "{\"newName\":\"ExistingName\"}";

        Response response = target("/groups/" + GROUP_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }
}
