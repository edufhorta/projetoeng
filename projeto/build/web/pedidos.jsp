<a href="index.jsp">Voltar</a>
<h1>Fazer Pedido</h1>
<form action="PedidoServlet" method="post">
    Refeição: <input type="text" name="refeicao"><br>
    <input type="submit" value="Pedir">
</form>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="banco.DAO" %>
<%@ page import="java.util.*" %>
<%
    DAO dao = new DAO();
    List<String> pedidos = null;
    try {
        dao.conectar();
        pedidos = dao.listar_pedidos();
    } catch (Exception e) {
        out.println("Erro: " + e.getMessage());
    } finally {
        dao.desconectar();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pedidos</title>
</head>
<body>
<h1>Pedidos</h1>
<% if (pedidos != null && !pedidos.isEmpty()) { %>
    <ul>
    <% for (String pedido : pedidos) { %>
        <li><%= pedido %></li>
    <% } %>
    </ul>
<% } else { %>
    <p>Nenhum pedido encontrado.</p>
<% } %>
</body>
</html>
