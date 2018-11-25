package nl.ertai.elytron.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.security.auth.SupportLevel;
import org.wildfly.security.auth.server.RealmIdentity;
import org.wildfly.security.auth.server.RealmUnavailableException;
import org.wildfly.security.credential.PasswordCredential;
import org.wildfly.security.evidence.Evidence;
import org.wildfly.security.evidence.PasswordGuessEvidence;
import org.wildfly.security.password.interfaces.ClearPassword;

import java.util.HashMap;
import java.util.Map;

class ExampleRealmTest {

    private static final String USER_NAME = "Test";
    private static final String USER_PASSWORD = "password";
    private static final String GROUP = "group";

    private ExampleRealm exampleRealm;

    @BeforeEach
    void init() {
        exampleRealm = new ExampleRealm();
        Map<String, String> configuration = new HashMap<>();
        configuration.put("User", USER_NAME);
        configuration.put("Password", USER_PASSWORD);
        configuration.put("Groups", GROUP);
        exampleRealm.initialize(configuration);
    }

    @Test
    void testGetRealmIdentityPrincipal() {
        Assertions.assertEquals(USER_NAME, getGoodRealmIdentity().getRealmIdentityPrincipal().getName());
    }

    @Test
    void testGetCredentialAcquireSupport() throws RealmUnavailableException {
        Assertions.assertEquals(SupportLevel.UNSUPPORTED, getGoodRealmIdentity().getCredentialAcquireSupport(PasswordCredential.class, ClearPassword.ALGORITHM_CLEAR, null));
    }

    @Test
    void testGetCredential() throws RealmUnavailableException {
        Assertions.assertNull(getGoodRealmIdentity().getCredential(PasswordCredential.class));
    }

    @Test
    void testGetEvidenceVerifySupportGoodRealmIdentity() throws RealmUnavailableException {
        Assertions.assertEquals(SupportLevel.POSSIBLY_SUPPORTED, getGoodRealmIdentity().getEvidenceVerifySupport(PasswordGuessEvidence.class, null));
    }

    @Test
    void testGetEvidenceVerifySupportBadRealmIdentity() throws RealmUnavailableException {
        Assertions.assertEquals(SupportLevel.UNSUPPORTED, getGoodRealmIdentity().getEvidenceVerifySupport(Evidence.class, null));
    }

    @Test
    void testVerifyEvidenceWithCorrectEvidence() throws RealmUnavailableException {
        Evidence evidence = createEvidence(USER_PASSWORD);
        Assertions.assertTrue(getGoodRealmIdentity().verifyEvidence(evidence));
    }

    @Test
    void testVerifyEvidenceWithInCorrectEvidence() throws RealmUnavailableException {
        Evidence evidence = createEvidence("notValid");
        Assertions.assertFalse(getGoodRealmIdentity().verifyEvidence(evidence));
    }

    @Test
    void testExistsWithCorrectUser() throws RealmUnavailableException {
        Assertions.assertTrue(getGoodRealmIdentity().exists());
    }

    @Test
    void testExistsWithUnknownUser() throws RealmUnavailableException {
        Assertions.assertFalse(exampleRealm.getRealmIdentity(() -> "AltTest").exists());
    }

    @Test
    void testGetAuthorizationIdentity() throws RealmUnavailableException {
        Assertions.assertEquals(GROUP, getGoodRealmIdentity().getAuthorizationIdentity().getAttributes().get("groups").get(0));
    }

    private Evidence createEvidence(String password) {
        return new PasswordGuessEvidence(password.toCharArray());
    }

    private RealmIdentity getGoodRealmIdentity() {
        return exampleRealm.getRealmIdentity(() -> USER_NAME);
    }

}
