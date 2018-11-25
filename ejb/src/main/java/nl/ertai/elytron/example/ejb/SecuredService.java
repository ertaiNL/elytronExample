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
    public static final String OTHER_ROLE = "Other";
    public static final String MAPPED_ROLE = "Mapped";
    public static final String UNMAPPED_ROLE = "Unmapped";

    private static final String ROLES_ALLOWED_TEXT = "User has %s Role in EJB";

    @Resource
    private SessionContext context;

    @RolesAllowed(ADMIN_ROLE)
    public String getAdminSecuredData() {
        return String.format(ROLES_ALLOWED_TEXT, ADMIN_ROLE);
    }

    @RolesAllowed(USER_ROLE)
    public String getUserSecuredData() {
        return String.format(ROLES_ALLOWED_TEXT, USER_ROLE);
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

    @PermitAll
    public boolean hasOtherRole() {
        return context.isCallerInRole(OTHER_ROLE);
    }

    @PermitAll
    public boolean hasMappedRole() {
        return context.isCallerInRole(MAPPED_ROLE);
    }

    @PermitAll
    public boolean hasUnmappedRole() {
        return context.isCallerInRole(UNMAPPED_ROLE);
    }
}
