package nl.ertai.elytron.example.webapp;

import nl.ertai.elytron.example.ejb.SecuredService;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static nl.ertai.elytron.example.ejb.SecuredService.ADMIN_ROLE;
import static nl.ertai.elytron.example.ejb.SecuredService.USER_ROLE;

@WebServlet("/secured")
@ServletSecurity(httpMethodConstraints = { @HttpMethodConstraint(value = "GET", rolesAllowed = { "User" }) })
public class SecuredServlet extends HttpServlet {

    private static final String RETURN = "<br />";

    @EJB
    private SecuredService securedService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            printPage(writer, request);
        } catch (IOException e) {
            //do nothing
        }

    }

    private void printPage(PrintWriter writer, HttpServletRequest request) {
        writer.println("<html>");
        writer.println("  <head><title>Secured Servlet</title></head>");
        writer.println("  <body>");
        writer.println("    <h1>Secured Servlet</h1>");
        writer.println("    <p>");
        writer.println("<b>Settings in Servlet</b>" + RETURN);
        writer.println("Username: "    + request.getUserPrincipal().getName() + RETURN);
        writer.println("User role?: "  + request.isUserInRole(USER_ROLE) + RETURN);
        writer.println("Admin role?: " + request.isUserInRole(ADMIN_ROLE) + RETURN);
        writer.println(RETURN);
        writer.println("<b>Settings in EJB</b>" + RETURN);
        writer.println("Username: "    + securedService.getUserName() + RETURN);
        writer.println("User role?: "  + securedService.hasUserRole() + RETURN);
        writer.println("Admin role?: " + securedService.hasAdminRole() + RETURN);
        writer.println("User data?: "  + getUserSecuredData() + RETURN);
        writer.println("Admin data?: " + getAdminSecuredData() + RETURN);
        writer.println("    </p>");
        writer.println("  </body>");
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
}
