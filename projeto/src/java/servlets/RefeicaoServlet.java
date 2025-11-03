package servlets;

import banco.DAO;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RefeicaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        DAO dao = new DAO();

        String nome = req.getParameter("nome");
        String[] ingredientes = req.getParameterValues("ingredientes");
        String[] quantidadesStr = req.getParameterValues("quantidades");

        List<Integer> quantidades = new ArrayList<>();
        if (quantidadesStr != null) {
            for (String q : quantidadesStr) {
                try {
                    quantidades.add(Integer.parseInt(q));
                } catch (NumberFormatException e) {
                    quantidades.add(0); // caso o valor não seja numérico
                }
            }
        }

        List<String> ingList = new ArrayList<>();
        if (ingredientes != null) {
            for (String i : ingredientes) ingList.add(i);
        }

        try {
            dao.conectar(); // conecta ao banco
            dao.cadastro_refeicao(nome, ingList, quantidades);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("erro", "Falha ao cadastrar refeição: " + e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("refeicoes.jsp");
            rd.forward(req, resp);
            return;
        } finally {
            dao.desconectar(); // garante desconexão
        }

        resp.sendRedirect("refeicoes.jsp");
    }
}
