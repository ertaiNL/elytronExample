package nl.ertai.elytron.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.auth.SupportLevel;
import org.wildfly.security.auth.server.RealmIdentity;
import org.wildfly.security.auth.server.SecurityRealm;
import org.wildfly.security.authz.AuthorizationIdentity;
import org.wildfly.security.authz.MapAttributes;
import org.wildfly.security.credential.Credential;
import org.wildfly.security.evidence.Evidence;
import org.wildfly.security.evidence.PasswordGuessEvidence;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.security.Principal;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Example of custom-realm for WildFly Elytron.
 * A SecurityRealm is the entity that holds the user-accounts and which roles each user has. The Realm get it's
 * configuration via JNDI-variables. It is also possible to get a datasource the same way if required.
 *
 * This class implements 2 interfaces:
 * - SecurityRealm -> This is the main part that enables Elytron to check the users
 *
 * In the SecurityRealm 3 functions are required:
 * - getCredentialAcquireSupport -> To enable Elytron to ask what credentials can be retrieved from this Realm
 * - getEvidenceVerifySupport ->    To enable Elytron to ask what evidence's can be checked with this Realm
 * - getRealmIdentity ->            To enable Elytron to get the realm-functionality for a principal.
 *
 * In the Configurable 1 function is required:
 * - initialize ->                  This function is called with the set parameters in the Elytron Configuration
 *
 * The principal in the realmIdentity that is given is normally the username of the user
 *
 *
 */
public class ExampleJNDIRealm implements SecurityRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleJNDIRealm.class);

    /**
     * The name of the attribute to set in the Identity with the groups in it
     */
    private static final String GROUPS_ATTRIBUTE = "groups";

    /**
     * The username of the user in this realm.
     */
    private String configuredUser;
    /**
     * The username of the user in this realm.
     */
    private String configuredPassword;
    /**
     * The groups that will be set for the user in this realm
     */
    private List<String> configuredGroups;

    /**
     * This constructor retrieves the JNDI-variables.
     */
    public ExampleJNDIRealm() {
        try {
            InitialContext ctx = new InitialContext();
            configuredUser = (String) ctx.lookup("java:/global/example/user");
            configuredPassword = (String) ctx.lookup("java:/global/example/password");

            String groups = (String) ctx.lookup("java:/global/example/groups");
            if (groups != null) {
                configuredGroups = Arrays.asList(groups.split(","));
            }
        } catch (NamingException e) {
            LOGGER.error("JNDI variables not found", e);
        }
    }

    /**
     * This realm does not allow acquiring credentials
     *
     * @param credentialType    The interface the possibly retrieved credential needs to implement
     * @param algorithmName     Does the Acquired Credential support passwords encrypted with this algorithm?
     * @param parameterSpec     Other parameters that the credentialType expects
     * @return Is this retrieving credentials with this credentialType, algorithm and parameterSpec supported?
     */
    public SupportLevel getCredentialAcquireSupport(Class<? extends Credential> credentialType, String algorithmName,
                                                    AlgorithmParameterSpec parameterSpec) {
        return SupportLevel.UNSUPPORTED;
    }

    /**
     * This realm will be able to verify password evidences only
     *
     * @param evidenceType      The interface that should be implemented
     * @param algorithmName     Does the VerifyEvidence support passwords encrypted with this algorithm?
     * @return Is VerifyEvidence with this evidenceType and algorithm supported?
     */
    public SupportLevel getEvidenceVerifySupport(Class<? extends Evidence> evidenceType, String algorithmName) {
        if (PasswordGuessEvidence.class.isAssignableFrom(evidenceType)) {
            return SupportLevel.POSSIBLY_SUPPORTED;
        } else {
            return SupportLevel.UNSUPPORTED;
        }
    }

    /**
     * Retrieve the realmIdentity set with the given principal.
     * The principal is most of the time the username
     *
     * @param principal     The principal of the user
     * @return The realmIdentity
     */
    @Override
    public RealmIdentity getRealmIdentity(final Principal principal) {

        return new RealmIdentity() {

            /**
             * To retrieve the Principal set for this RealmIdentity
             *
             * @return  The principal set
             */
            public Principal getRealmIdentityPrincipal() {
                return principal;
            }

            /**
             * This realmIdentity does not allow acquiring credentials
             *
             * @param credentialType    The interface the possibly retrieved credential needs to implement
             * @param algorithmName     Does the Acquired Credential support passwords encrypted with this algorithm?
             * @param parameterSpec     Other parameters that the credentialType expects
             * @return Is this retrieving credentials with this credentialType, algorithm and parameterSpec supported?
             */
            public SupportLevel getCredentialAcquireSupport(Class<? extends Credential> credentialType,
                                                            String algorithmName,
                                                            AlgorithmParameterSpec parameterSpec) {
                return ExampleJNDIRealm.this.getCredentialAcquireSupport(credentialType, algorithmName, parameterSpec);
            }

            /**
             * The function to retrieve the credentials. But as it isn't supported this realm returns null
             *
             * @param credentialType    The class of the interface
             * @param <C>               The interface the possibly retrieved credential needs to implement
             * @return                  Null in this realm.
             */
            public <C extends Credential> C getCredential(Class<C> credentialType) {
                return null;
            }

            /**
             * This realmIdentity will be able to verify password evidences only
             *
             * @param evidenceType      The interface that should be implemented
             * @param algorithmName     Does the VerifyEvidence support passwords encrypted with this algorithm?
             * @return      Is VerifyEvidence with this evidenceType and algorithm supported?
             */
            public SupportLevel getEvidenceVerifySupport(Class<? extends Evidence> evidenceType, String algorithmName) {
                return ExampleJNDIRealm.this.getEvidenceVerifySupport(evidenceType, algorithmName);
            }

            /**
             * This function checks if the principal can login.
             * The evidence can be encrypted for safety.
             *
             * @param evidence  The evidence-class with the password of the user.
             * @return          Is the password correct?
             */
            public boolean verifyEvidence(Evidence evidence) {
                if (evidence instanceof PasswordGuessEvidence) {
                    PasswordGuessEvidence guess = (PasswordGuessEvidence) evidence;
                    try {
                        return checkUserCredentials(principal.getName(), guess.getGuess());
                    } finally {
                        guess.destroy();
                    }
                }
                return false;
            }

            /**
             * This function defines if the user asked exists
             *
             * @return  Does the principal exists in this realm?
             */
            public boolean exists() {
                return principal.getName().equals(configuredUser);
            }

            /**
             * This will retrieve the AuthorizationIdentity with all the groups the user belongs to
             *
             * @return  The AuthorizationIdentity of the user
             */
            @Override
            public AuthorizationIdentity getAuthorizationIdentity() {
                Map<String, List<String>> groups = Collections.singletonMap(GROUPS_ATTRIBUTE, configuredGroups);
                return AuthorizationIdentity.basicIdentity(new MapAttributes(groups));
            }
        };
    }

    /**
     * Demo function that checks the username and password combo
     *
     * @param user          The name of the user
     * @param password      The password of the user
     * @return              Is this combo correct?
     */
    private boolean checkUserCredentials(final String user, final char[] password) {
        return ((configuredUser.equals(user)) && Arrays.equals(configuredPassword.toCharArray(), password));
    }

}
