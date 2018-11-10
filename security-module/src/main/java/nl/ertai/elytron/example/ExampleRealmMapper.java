package nl.ertai.elytron.example;

import org.wildfly.extension.elytron.Configurable;
import org.wildfly.security.evidence.Evidence;

import java.security.Principal;
import java.util.Map;

/**
 *  Example of custom-realm-mapper for WildFly Elytron.
 *  This entity decides which security-realm should be used for this user.
 */
public class ExampleRealmMapper implements org.wildfly.security.auth.server.RealmMapper, Configurable {

    private static final String ALTERNATIVE_REALM = "AlternativeRealm";

    private String alternativeUsersStartWith;

    @Override
    public void initialize(Map<String, String> configuration) {
        alternativeUsersStartWith = configuration.get("AlternativeUsersStartWith");
    }

    @Override
    public String getRealmMapping(Principal principal, Evidence evidence) {
        if (alternativeUsersStartWith != null && principal.getName().startsWith(alternativeUsersStartWith)) {
            return ALTERNATIVE_REALM;
        } else {
            return null;
        }
    }

}
