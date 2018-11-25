package nl.ertai.elytron.example;

import org.wildfly.extension.elytron.Configurable;
import org.wildfly.security.auth.server.RealmMapper;
import org.wildfly.security.evidence.Evidence;

import java.security.Principal;
import java.util.Map;

/**
 *  Example of custom-realm-mapper for WildFly Elytron.
 *  This entity decides which security-realm should be used for this user.
 */
public class ExampleRealmMapper implements RealmMapper, Configurable {

    /**
     * The user we want to map to a different realm then the default.
     */
    private String userToMap;

    /**
     * The realm to map the userToMap to.
     */
    private String realmToMapTo;

    /**
     * This function allows you to use the parameters given in the Elytron Configuration to influence your realmMapper
     * This function will be called when JBoss/Wildfly will startup. Please remember that not many other services,
     * like the Datasource, are available yet.
     *
     * @param configuration A Key-Value-Map with all the parameters
     */
    @Override
    public void initialize(Map<String, String> configuration) {
        userToMap = configuration.get("UserToMap");
        realmToMapTo = configuration.get("RealmToMapTo");
    }

    /**
     * This function is called when a call is made. Each call decides again which realm to look for.
     * If the string returns is not a defined SecurityRealm then the default will be picked.
     *
     * @param principal     The user that is logged in.
     * @param evidence      This is null in the scenario's I tested
     * @return              The realm to select or null if default should be picked
     */
    @Override
    public String getRealmMapping(Principal principal, Evidence evidence) {
        if (userToMap != null && principal.getName().equals(userToMap)) {
            return realmToMapTo;
        } else {
            return null;
        }
    }

}
