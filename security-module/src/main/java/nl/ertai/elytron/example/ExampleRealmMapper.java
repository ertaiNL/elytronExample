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

    private static final String ALTERNATIVE_REALM = "AlternativeRealm";

    private String alternativeUsersStartWith;

    /**
     * This function allows you to use the parameters given in the Elytron Configuration to influence your realmMapper
     * This function will be called when JBoss/Wildfly will startup. Please remember that not many other services,
     * like the Datasource, are available yet.
     *
     * @param configuration A Key-Value-Map with all the parameters
     */
    @Override
    public void initialize(Map<String, String> configuration) {
        alternativeUsersStartWith = configuration.get("AlternativeUsersStartWith");
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
        if (alternativeUsersStartWith != null && principal.getName().startsWith(alternativeUsersStartWith)) {
            return ALTERNATIVE_REALM;
        } else {
            return null;
        }
    }

}
