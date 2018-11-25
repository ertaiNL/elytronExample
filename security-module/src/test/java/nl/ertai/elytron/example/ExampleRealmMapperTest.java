package nl.ertai.elytron.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.security.evidence.Evidence;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

class ExampleRealmMapperTest {

    private static final Evidence EVIDENCE = null;
    private static final String ALTERNATIVE_REALM = "AlternativeRealm";
    private static final Principal USER = () -> "Test";
    private static final Principal ALTERNATIVE_USER = () -> "AltTest";
    private static final Principal NONE_USER = () -> "NoneUser";

    private ExampleRealmMapper exampleRealmMapper;

    @BeforeEach
    void init() {
        exampleRealmMapper = new ExampleRealmMapper();
    }

    @Test
    void testDefaultRealm() {
        configureExampleRealmMapper();
        Assertions.assertNull(exampleRealmMapper.getRealmMapping(NONE_USER, EVIDENCE));
    }

    @Test
    void testNormalRealm() {
        configureExampleRealmMapper();
        Assertions.assertNull(exampleRealmMapper.getRealmMapping(USER, EVIDENCE));
    }

    @Test
    void testAlternativeRealm() {
        configureExampleRealmMapper();
        Assertions.assertEquals(ALTERNATIVE_REALM, exampleRealmMapper.getRealmMapping(ALTERNATIVE_USER, EVIDENCE));
    }

    @Test
    void testNoUserSet() {
        Assertions.assertNull(exampleRealmMapper.getRealmMapping(USER, EVIDENCE));
    }

    private void configureExampleRealmMapper() {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("UserToMap", ALTERNATIVE_USER.getName());
        configuration.put("RealmToMapTo", ALTERNATIVE_REALM);
        exampleRealmMapper.initialize(configuration);
    }

}
