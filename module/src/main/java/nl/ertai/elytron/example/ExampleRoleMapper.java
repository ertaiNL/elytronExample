package nl.ertai.elytron.example;

import org.wildfly.extension.elytron.Configurable;
import org.wildfly.security.authz.RoleMapper;
import org.wildfly.security.authz.Roles;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleRoleMapper implements RoleMapper, Configurable {

    private String roleToMap;
    private String roleToMapTo;

    /**
     * This function allows you to use the parameters given in the Elytron Configuration to influence your RoleMapper
     * This function will be called when JBoss/Wildfly will startup. Please remember that not many other services,
     * like the Datasource, are available yet.
     *
     * @param configuration A Key-Value-Map with all the parameters
     */
    public void initialize(Map<String, String> configuration) {
        roleToMap = configuration.get("RoleToMap");
        roleToMapTo = configuration.get("RoleToMapTo");
    }

    /**
     * This function is called after the identity is determened by the Realm so that you can map roles.
     * In this case the role given in the configuration is removed and a rol 'Mapped' is added
     *
     * @param roles     The roles of the user determined by the realm
     * @return          The roles the user that will be given to it
     */
    @Override
    public Roles mapRoles(Roles roles) {
        Set<String> newRoles = new HashSet<>();
        roles.forEach(role -> newRoles.add(convertRole(role)));
        return Roles.fromSet(newRoles);
    }

    private String convertRole(String role) {
        return role.equals(roleToMap) ? roleToMapTo : role;
    }
}
