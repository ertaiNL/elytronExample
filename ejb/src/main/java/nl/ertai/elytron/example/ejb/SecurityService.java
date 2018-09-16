package nl.ertai.elytron.example.ejb;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.ejb.Stateless;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Set;

@Stateless
public class SecurityService {

    public Boolean isUserInRole(String role) {
        Subject subject = getSubjectClass();
        Set<SimpleGroup> principals = subject.getPrincipals(SimpleGroup.class);
        for (SimpleGroup principal : principals) {
            if (principal.getName().equals("Roles")) {
                Enumeration<Principal> enumeration = principal.members();
                while (enumeration.hasMoreElements()) {
                    if (enumeration.nextElement().getName().equals(role)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getUserName() {
        return getUserPrincipal().getName();
    }

    private Principal getUserPrincipal() {
        Subject subject = getSubjectClass();
        Set<SimplePrincipal> principals = subject.getPrincipals(SimplePrincipal.class);
        if (principals.isEmpty()) {
            throw new IllegalStateException("No SimplePrincipal found");
        }
        return principals.iterator().next();
    }

    private Subject getSubjectClass() {
        try {
            Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
            if (subject == null) {
                throw new IllegalStateException("Subject not found in PolicyContext");
            }
            return subject;
        } catch (PolicyContextException e) {
            throw new IllegalStateException("PolicyContext not retrieved");
        }
    }

}
