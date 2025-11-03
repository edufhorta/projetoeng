<a type="jsp" href="index.jsp">Voltar</a>
<h1>Enviar Feedback</h1>
<form action="FeedbackServlet" method="post">
    Refeição: <input type="text" name="refeicao"><br>
    Comentário: <input type="text" name="comentario"><br>
    <input type="submit" value="Enviar">
</form>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="banco.DAO" %>
<%@ page import="java.util.*" %>
<%
    DAO dao = new DAO();
    List<String> feedbacks = null;
    try {
        dao.conectar();
        feedbacks = dao.listar_feedbacks();
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
    <title>Feedbacks</title>
</head>
<body>
<h1>Feedbacks</h1>
<% if (feedbacks != null && !feedbacks.isEmpty()) { %>
    <ul>
    <% for (String fb : feedbacks) { %>
        <li><%= fb %></li>
    <% } %>
    </ul>
<% } else { %>
    <p>Nenhum feedback encontrado.</p>
<% } %>
</body>
</html>
