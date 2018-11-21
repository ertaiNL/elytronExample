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
    private static final String PARAMETER_ALTERNATIVE_USER = "AlternativeUser";
    private static final Principal USER = () -> "Test";
    private static final Principal ALTERNATIVE_USER = () -> "AltTest";

    private ExampleRealmMapper exampleRealmMapper;

    @BeforeEach
    void init() {
        exampleRealmMapper = new ExampleRealmMapper();
        Map<String, String> configuration = new HashMap<>();
        configuration.put(PARAMETER_ALTERNATIVE_USER, ALTERNATIVE_USER.getName());
        exampleRealmMapper.initialize(configuration);
    }

    @Test
    void testDefaultRealm() {
        Assertions.assertNull(exampleRealmMapper.getRealmMapping(USER, EVIDENCE));
    }

    @Test
    void testAlternativeRealm() {
        Assertions.assertEquals(ALTERNATIVE_REALM, exampleRealmMapper.getRealmMapping(ALTERNATIVE_USER, EVIDENCE));
    }

}
