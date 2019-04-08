package nl.ertai.elytron.example.webapp;

import nl.ertai.elytron.example.ejb.SecuredService;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Optional;

@WebServlet("/")
@ServletSecurity(httpMethodConstraints = { @HttpMethodConstraint(value = "GET", rolesAllowed = { "User" }) })
public class SecuredServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String RETURN = "<br />";

    @EJB
    private SecuredService securedService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getParameterValues("logout") != null) {
                request.logout();
            }
            PrintWriter writer = response.getWriter();
            printPage(writer, request);
        } catch (IOException | ServletException e) {
            //do nothing
        }

    }

    private void printPage(PrintWriter writer, HttpServletRequest request) {
        writer.println("<html>");
        writer.println("<head><title>Secured Servlet</title></head>");
        writer.println("<body>");
        writer.println("<h1>Secured Servlet</h1>");
        writer.println("<p>");
        writer.println("<b>Settings in Servlet</b>" + RETURN);
        writer.println("Username: "    + getUserNameFromPrincipal(request).orElse("anonymous") + RETURN);
        writer.println("User role?: "  + request.isUserInRole(SecuredService.USER_ROLE) + RETURN);
        writer.println("Admin role?: " + request.isUserInRole(SecuredService.ADMIN_ROLE) + RETURN);
        writer.println("Other role?: " + request.isUserInRole(SecuredService.OTHER_ROLE) + RETURN);
        writer.println("Mapped role?: " + request.isUserInRole(SecuredService.MAPPED_ROLE) + RETURN);
        writer.println("Unmapped role?: " + request.isUserInRole(SecuredService.UNMAPPED_ROLE) + RETURN);
        writer.println(RETURN);
        writer.println("<b>Settings in EJB</b>" + RETURN);
        writer.println("Username: "    + securedService.getUserName() + RETURN);
        writer.println("User role?: "  + securedService.hasUserRole() + RETURN);
        writer.println("Admin role?: " + securedService.hasAdminRole() + RETURN);
        writer.println("Other role?: " + securedService.hasOtherRole() + RETURN);
        writer.println("Mapped role?: " + securedService.hasMappedRole() + RETURN);
        writer.println("Unmapped role?: " + securedService.hasUnmappedRole() + RETURN);
        writer.println("User data?: "  + getUserSecuredData() + RETURN);
        writer.println("Admin data?: " + getAdminSecuredData() + RETURN);
        writer.println("</p>");
        writer.println(getAuthenticationLink(request));
        writer.println("</body>");
        writer.println("</html>");
    }

    private String getUserSecuredData() {
        try {
            return securedService.getUserSecuredData();
        } catch (EJBAccessException e) {
            return "Not Allowed";
        }
    }

    private String getAdminSecuredData() {
        try {
            return securedService.getAdminSecuredData();
        } catch (EJBAccessException e) {
            return "Not Allowed";
        }
    }

    private static Optional<String> getUserNameFromPrincipal(HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        return p != null ? Optional.of(p.getName()) : Optional.empty();
    }

    private static String getAuthenticationLink(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "<a href='/?logout=logout'>Logout</a>";
        } else {
            return "<a href='/secured'>Login</a>";
        }
    }
}
