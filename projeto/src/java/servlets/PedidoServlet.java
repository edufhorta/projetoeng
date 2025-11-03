package servlets;

import banco.DAO;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

public class PedidoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String refeicao = req.getParameter("refeicao");
        DAO dao = new DAO();

        try {
            dao.conectar(); // conecta ao banco
            dao.fazer_pedido(refeicao);
        } catch (SQLException e) {
            e.printStackTrace();
            // opcional: enviar mensagem de erro para o JSP
            req.setAttribute("erro", "Falha ao fazer pedido: " + e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("pedidos.jsp");
            rd.forward(req, resp);
            return;
        } finally {
            dao.desconectar(); // garante desconexão
        }

        // Se tudo certo, redireciona
        resp.sendRedirect("pedidos.jsp");
    }
}
