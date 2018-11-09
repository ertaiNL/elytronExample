package nl.ertai.elytron.example.ejb;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.ejb.Stateless;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Stateless
public class SecurityService {

    /**
     * This function retrieves the username of the user from JBoss/Wildfly
     *
     * @return The username
     */
    public String getUserName() {
        return getUserPrincipal().getName();
    }

    /**
     * This function gets the roles that the user has been assigned by the securityRealm
     *
     * @return The roles
     */
    public List<String> getUserRoles() {
        List<String> roles = new ArrayList<>();
        Collections.list(getRolesPrincipal().members()).forEach(e -> roles.add(e.getName()));
        return roles;
    }

    /**
     * This function specifically gets the roles-principal
     *
     * @return  The group-principal
     */
    private SimpleGroup getRolesPrincipal() {
        Set<SimpleGroup> principals = getPrincipals(SimpleGroup.class);
        for (SimpleGroup principal : principals) {
            if (principal.getName().equals("Roles")) {
                return principal;
            }
        }
        throw new IllegalStateException("No RolesPrincipal found");
    }

    /**
     * This function specifically gets the user-principal
     *
     * @return  The user-principal
     */
    private Principal getUserPrincipal() {
        Set<SimplePrincipal> principals = getPrincipals(SimplePrincipal.class);
        if (principals.isEmpty()) {
            throw new IllegalStateException("No UserPrincipal found");
        }
        return principals.iterator().next();
    }

    /**
     * This function gets the principals of the type that you want
     *
     * @param c         The class-object of the type to find
     * @param <T>       The Type of class to find
     * @return          The Principals of that type
     */
    private <T extends Principal> Set<T> getPrincipals(Class<T> c) {
        Subject subject = getSubject();
        if (subject == null) {
            throw new IllegalStateException("Subject not found in PolicyContext");
        }
        return subject.getPrincipals(c);
    }

    /**
     * This function gets the subject. This subject contains all the security-properties of the user
     *
     * @return  The Subject or Null if it doesn't exist
     */
    private Subject getSubject() {
        try {
            return (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        } catch (PolicyContextException e) {
            throw new IllegalStateException("PolicyContext not retrieved");
        }
    }

}
