package ca.ulaval.glo2003.resources;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo2003.routes.group.infra.GroupPersistenceInMemory;
import ca.ulaval.glo2003.routes.group.logic.Group;
import ca.ulaval.glo2003.routes.group.logic.IGroup;
import ca.ulaval.glo2003.routes.member.api.MemberResource;
import ca.ulaval.glo2003.routes.member.infra.MemberPersistenceInMemory;
import ca.ulaval.glo2003.routes.member.logic.Member;
import ca.ulaval.glo2003.shared.infra.Groups;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberResourceTest extends JerseyTest {

    private static final String GROUP_NAME = "Fantastics";
    private static final String MEMBER_NAME = "Goldman";
    private static final String NOT_SAME_MEMBER_NAME = "Grayman";
    private static final String INVALID_NEW_NAME_PUT_JSON = "{\"newMemberName\":\"Grayman\"}";
    private static final String INVALID_NEW_NAME_SPACES_PUT_JSON = "{\"newMemberName\":\"renamed Name\"}";
    private static final String VALID_NEW_NAME_PUT_JSON = "{\"newMemberName\":\"renamedName\"}";
    private static final String LOCATION_HEADER_KEY_NAME = "Location";
    private static final String LOCATION_HEADER_VALUE =
        "http://0.0.0.0:null/groups/Fantastics/members/renamedName";
    private final String MEMBER_HTTP_HEADER = "Member";
    private final int EXPECTED_MEMBERS_SIZE = 1;
    private final Groups groups = Groups.getInstance();

    @Override
    protected Application configure() {
        return new ResourceConfig()
            .register(new MemberResource(new MemberPersistenceInMemory(), new GroupPersistenceInMemory()));
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        groups.getGroups().clear();
        Group group = new Group(GROUP_NAME, new ArrayList<>(), new ArrayList<>());
        groups.getGroups().add(group);
    }

    @Test
    void givenValidGroup_whenListingMembers_thenReturns403() {
        Response response = target("/groups/" + GROUP_NAME + "/members").request().get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void givenGroupWithMembers_whenListingMembers_thenReturnsMembersList() {
        IGroup group = groups.getExistingGroup(GROUP_NAME);
        group.addMember(new Member(MEMBER_NAME));

        Response response = target("/groups/" + GROUP_NAME + "/members")
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Map<String, Object>> members = response.readEntity(List.class);
        Assertions.assertFalse(members.isEmpty());
        assertEquals(EXPECTED_MEMBERS_SIZE, members.size());
    }

    @Test
    void givenNonExistentGroup_whenListingMembers_thenReturns404() {
        Response response = target("/groups/nonexistentGroup/members").request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void givenInvalidMember_whenListingMembers_thenReturns403() {
        Response response = target("/groups/" + GROUP_NAME + "/members")
            .request()
            .header(MEMBER_HTTP_HEADER, "nonexistentMember")
            .get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void givenValidMember_whenListingMembers_thenReturnsOK() {
        IGroup group = groups.getExistingGroup(GROUP_NAME);
        group.addMember(new Member(MEMBER_NAME));

        Response response = target("/groups/" + GROUP_NAME + "/members")
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void givenMember_whenRenamingMember_thenReturnsCreated() {
        IGroup group = groups.getExistingGroup(GROUP_NAME);
        group.addMember(new Member(MEMBER_NAME));

        Response response = target("/groups/" + GROUP_NAME + "/members/" + MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.json(VALID_NEW_NAME_PUT_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(LOCATION_HEADER_VALUE, response.getHeaders().get(LOCATION_HEADER_KEY_NAME).getFirst());
    }

    @Test
    void givenMemberNotInGroup_whenRenamingMember_thenReturns403() {
        Response response = target("/groups/" + GROUP_NAME + "/members/" + MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.json(VALID_NEW_NAME_PUT_JSON));

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void givenMemberNameDifferentThanHeaderMemberName_whenRenamingMember_thenReturns409() {
        IGroup group = groups.getExistingGroup(GROUP_NAME);
        group.addMember(new Member(MEMBER_NAME));

        Response response = target("/groups/" + GROUP_NAME + "/members/" + NOT_SAME_MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.json(VALID_NEW_NAME_PUT_JSON));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void givenMember_whenRenamingMemberWithSpacesInNewName_thenReturns400() {
        IGroup group = groups.getExistingGroup(GROUP_NAME);
        group.addMember(new Member(MEMBER_NAME));

        Response response = target("/groups/" + GROUP_NAME + "/members/" + MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.json(INVALID_NEW_NAME_SPACES_PUT_JSON));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void givenMembers_whenRenamingMemberOneWithNameOfMemberTwo_thenReturns409() {
        IGroup group = groups.getExistingGroup(GROUP_NAME);
        group.addMember(new Member(MEMBER_NAME));
        group.addMember(new Member(NOT_SAME_MEMBER_NAME));

        Response response = target("/groups/" + GROUP_NAME + "/members/" + MEMBER_NAME)
            .request()
            .header(MEMBER_HTTP_HEADER, MEMBER_NAME)
            .put(Entity.json(INVALID_NEW_NAME_PUT_JSON));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }
}
