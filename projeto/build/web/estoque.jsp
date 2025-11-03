<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="banco.DAO" %>
<%@ page import="java.util.*" %>
<%
    DAO dao = new DAO();
    Map<String, Integer> estoque = new LinkedHashMap<>();
    try {
        dao.conectar();
        estoque = dao.listar_estoque();
    } catch (Exception e) {
        out.println("<p style='color:red;'>Erro ao acessar o banco: " + e.getMessage() + "</p>");
    } finally {
        dao.desconectar();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
<a href="index.jsp">Voltar</a>
    <title>Estoque</title>
    <style>
        table { border-collapse: collapse; width: 50%; margin-top: 20px; }
        th, td { border: 1px solid #333; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <h1>Estoque</h1>

    <table>
        <tr>
            <th>Ingrediente</th>
            <th>Quantidade</th>
        </tr>
        <% 
            if (estoque != null && !estoque.isEmpty()) {
                for (Map.Entry<String, Integer> entry : estoque.entrySet()) {
                    String ingredientes = entry.getKey();
                    Integer quantidade = entry.getValue();

                    // separar por vírgula e mostrar cada um em linha
                    String[] itens = ingredientes.split(",");
                    for(String item : itens) {
                        item = item.trim(); // remove espaços extras
        %>
        <tr>
            <td><%= item %></td>
            <td><%= quantidade %></td>
        </tr>
        <%              } // fim for itens
                } // fim for entry
            } else { 
        %>
        <tr>
            <td colspan="2">Não há ingredientes cadastrados.</td>
        </tr>
        <% } %>
    </table>
</body>
</html>
