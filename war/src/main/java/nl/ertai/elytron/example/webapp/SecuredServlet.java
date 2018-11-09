package nl.ertai.elytron.example.webapp;

import nl.ertai.elytron.example.ejb.SecurityService;

import javax.ejb.EJB;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/secured")
@ServletSecurity(httpMethodConstraints = { @HttpMethodConstraint(value = "GET", rolesAllowed = { "User" }) })
public class SecuredServlet extends HttpServlet {

    private static final String RETURN = "<br />";

    @EJB
    private SecurityService securityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            printPage(writer);
        } catch (IOException e) {
            //do nothing
        }

    }

    private void printPage(PrintWriter writer) {
        writer.println("<html>");
        writer.println("  <head><title>Secured Servlet</title></head>");
        writer.println("  <body>");
        writer.println("    <h1>Secured Servlet</h1>");
        writer.println("    <p>");
        writer.println("EJB Principal: " + securityService.getUserName() + RETURN);
        writer.println("EJB Roles: "     + securityService.getUserRoles().toString() + RETURN);
        writer.println("    </p>");
        writer.println("  </body>");
        writer.println("</html>");
    }
}
