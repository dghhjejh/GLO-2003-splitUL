package ca.ulaval.glo2003.shared.infra;

import ca.ulaval.glo2003.routes.group.logic.IGroup;
import java.util.ArrayList;

public class Groups {

    private static Groups instance;
    private final ArrayList<ca.ulaval.glo2003.routes.group.logic.IGroup> groups = new ArrayList<>();

    public static Groups getInstance() {
        if (instance == null) {
            instance = new Groups();
        }
        return instance;
    }

    public ArrayList<IGroup> getGroups() {
        return this.groups;
    }

    public IGroup getExistingGroup(String groupName) {
        return this.groups.stream().filter(g -> g.getName().equals(groupName)).findAny().orElse(null);
    }
}
