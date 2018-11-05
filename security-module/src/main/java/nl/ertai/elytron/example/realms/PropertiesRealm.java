package nl.ertai.elytron.example.realms;

import org.wildfly.security.auth.SupportLevel;
import org.wildfly.security.auth.server.RealmIdentity;
import org.wildfly.security.auth.server.SecurityRealm;
import org.wildfly.security.authz.AuthorizationIdentity;
import org.wildfly.security.authz.MapAttributes;
import org.wildfly.security.credential.Credential;
import org.wildfly.security.evidence.Evidence;
import org.wildfly.security.evidence.PasswordGuessEvidence;

import java.security.Principal;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collections;

/**
 * Example of custom-realm for WildFly Elytron.
 *
 * In this class 3 functions are implemented.
 * - getCredentialAcquireSupport -> To enable Elytron to ask what credentials can be retrieved from this Realm
 * - getEvidenceVerifySupport ->    To enable Elytron to ask what evidence's can be checked with this Realm
 * - getRealmIdentity ->            To enable Elytron to get the realm-functionality for a principal.
 *                                  This is the main functionality of the realm
 *
 * The principal in the realmIdentity that is given is normally the username of the user
 */
public class PropertiesRealm implements SecurityRealm {

    private static final String GROUPS_ATTRIBUTE = "groups";

    /**
     * This realm does not allow acquiring credentials
     *
     * @param credentialType    The interface the possibly retrieved credential needs to implement
     * @param algorithmName     Does the Acquired Credential support passwords encrypted with this algorithm?
     * @param parameterSpec     Other parameters that the credentialType expects
     * @return      Is this retrieving credentials with this credentialType, algorithm and parameterSpec supported?
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
     * @return      Is VerifyEvidence with this evidenceType and algorithm supported?
     */
    public SupportLevel getEvidenceVerifySupport(Class<? extends Evidence> evidenceType, String algorithmName) {
        return PasswordGuessEvidence.class.isAssignableFrom(evidenceType) ? SupportLevel.POSSIBLY_SUPPORTED : SupportLevel.UNSUPPORTED;
    }

    /**
     * Retrieve the realmIdentity set with the given principal.
     * The principal is most of the time the username
     *
     * @param principal     The principal of the user
     * @return              The realmIdentity
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
             * @return      Is this retrieving credentials with this credentialType, algorithm and parameterSpec supported?
             */
            public SupportLevel getCredentialAcquireSupport(Class<? extends Credential> credentialType,
                                                            String algorithmName, AlgorithmParameterSpec parameterSpec) {
                return PropertiesRealm.this.getCredentialAcquireSupport(credentialType, algorithmName, parameterSpec);
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
                return PropertiesRealm.this.getEvidenceVerifySupport(evidenceType, algorithmName);
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
             * This realmIdentity states that the user always exist.
             *
             * @return  Does the principal exists in this realm?
             */
            public boolean exists() {
                return true;
            }

            /**
             * This will retrieve the AuthorizationIdentity with all the groups the user belongs to
             *
             * @return  The AuthorizationIdentity of the user
             */
            @Override
            public AuthorizationIdentity getAuthorizationIdentity() {
                return AuthorizationIdentity.basicIdentity(new MapAttributes(Collections.singletonMap(GROUPS_ATTRIBUTE, Collections.singleton( "groupName" ))));
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
        return (("admin".equals(user)) && Arrays.equals("admin".toCharArray(), password));
    }

}
