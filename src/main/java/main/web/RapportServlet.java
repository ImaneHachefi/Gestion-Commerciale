package main.web;

import main.service.RapportService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/rapport")
public class RapportServlet extends HttpServlet {
    private final RapportService rapportService = new RapportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        List<Object[]> lignes = rapportService.chiffreAffairesMoisPrecedent();
        try (PrintWriter out = resp.getWriter()) {
            out.println("<h2>Chiffre d'affaires du mois précédent par famille</h2>");
            out.println("<table border='1' cellpadding='6'><tr><th>Famille</th><th>CA</th></tr>");
            for (Object[] l : lignes) {
                out.println("<tr><td>" + l[0] + "</td><td>" + l[1] + "</td></tr>");
            }
            out.println("</table>");
        }
    }
}
