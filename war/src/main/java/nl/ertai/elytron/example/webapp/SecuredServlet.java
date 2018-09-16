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
import java.security.Principal;

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
            printPage(request, writer);
        } catch (IOException e) {
            //do nothing
        }

    }

    private void printPage(HttpServletRequest request, PrintWriter writer) {
        writer.println("<html>");
        writer.println("  <head><title>Secured Servlet</title></head>");
        writer.println("  <body>");
        writer.println("    <h1>Secured Servlet</h1>");
        writer.println("    <p>");
        writer.println("Request Principal: " + addQuotes(getUserName(request)) + RETURN);
        writer.println("Request Role Guest: " + addQuotes(request.isUserInRole("Guest")) + RETURN);
        writer.println("Request Role User: " + addQuotes(request.isUserInRole("User")) + RETURN);
        writer.println(RETURN);
        writer.println("EJB Principal: " + addQuotes(securityService.getUserName()) + RETURN);
        writer.println("EJB Role Guest: " + addQuotes(securityService.isUserInRole("Guest")) + RETURN);
        writer.println("EJB Role User: " + addQuotes(securityService.isUserInRole("User")) + RETURN);
        writer.println("    </p>");
        writer.println("  </body>");
        writer.println("</html>");
    }

    private String addQuotes(Boolean value) {
        return "'" + value + "'";
    }

    private String addQuotes(String text) {
        return "'" + text + "'";
    }

    private String getUserName(HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        return user != null ? user.getName() : "NO AUTHENTICATED USER";
    }
}
