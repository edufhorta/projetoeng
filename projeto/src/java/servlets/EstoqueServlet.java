package servlets;

import banco.DAO;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class EstoqueServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        DAO dao = new DAO();
        try {
            dao.conectar(); // conecta antes
            Map<String, Integer> estoque = dao.listar_estoque(); // retorna o estoque
            req.setAttribute("estoque", estoque); // envia para JSP
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("erro", "Falha ao listar estoque: " + e.getMessage());
        } finally {
            dao.desconectar(); // garante desconexão
        }

        RequestDispatcher rd = req.getRequestDispatcher("estoque.jsp");
        rd.forward(req, resp);
    }
}