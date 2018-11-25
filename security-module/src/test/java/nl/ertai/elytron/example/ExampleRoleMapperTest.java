package nl.ertai.elytron.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.security.authz.Roles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class ExampleRoleMapperTest {

    private static final String ADMIN_ROLE_NAME = "Admin";
    private static final String USER_ROLE_NAME = "User";
    private static final String MAPPER_ROLE_NAME = "Mapped";

    private ExampleRoleMapper exampleRoleMapper;

    @BeforeEach
    void init() {
        exampleRoleMapper = new ExampleRoleMapper();
        Map<String, String> configuration = new HashMap<>();
        configuration.put("RoleToMap", USER_ROLE_NAME);
        configuration.put("RoleToMapTo", MAPPER_ROLE_NAME);
        exampleRoleMapper.initialize(configuration);
    }

    @Test
    void testMapRolesWithOtherRole() {
        Roles result = exampleRoleMapper.mapRoles(Roles.fromSet(new HashSet<>(Arrays.asList(ADMIN_ROLE_NAME, USER_ROLE_NAME))));
        Assertions.assertTrue(result.contains(MAPPER_ROLE_NAME));
        Assertions.assertTrue(result.contains(ADMIN_ROLE_NAME));
        Assertions.assertFalse(result.contains(USER_ROLE_NAME));
    }

    @Test
    void testMapRolesWithoutOtherRole() {
        Roles result = exampleRoleMapper.mapRoles(Roles.of(ADMIN_ROLE_NAME));
        Assertions.assertFalse(result.contains(MAPPER_ROLE_NAME));
        Assertions.assertTrue(result.contains(ADMIN_ROLE_NAME));
    }

}
