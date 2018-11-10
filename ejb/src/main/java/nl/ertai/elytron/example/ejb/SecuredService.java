package nl.ertai.elytron.example.ejb;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class SecuredService {

    public static final String ADMIN_ROLE = "Admin";
    public static final String USER_ROLE = "User";

    @Resource
    private SessionContext context;

    @RolesAllowed(ADMIN_ROLE)
    public String getAdminSecuredData() {
        return "User has " + ADMIN_ROLE + " Role in EJB";
    }

    @RolesAllowed(USER_ROLE)
    public String getUserSecuredData() {
        return "User has " + USER_ROLE + " Role in EJB";
    }

    @PermitAll
    public String getUserName() {
        return context.getCallerPrincipal().getName();
    }

    @PermitAll
    public boolean hasUserRole() {
        return context.isCallerInRole(USER_ROLE);
    }

    @PermitAll
    public boolean hasAdminRole() {
        return context.isCallerInRole(ADMIN_ROLE);
    }

}
