<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="banco.DAO" %>
<%@ page import="java.util.*" %>
<%
    DAO dao = new DAO();
    List<String> refeicoes = null;
    String mensagem = (String) request.getAttribute("mensagem");
    try {
        dao.conectar(); // conecta
        refeicoes = dao.listar_refeicoes();
    } catch (Exception e) {
        out.println("Erro: " + e.getMessage());
    } finally {
        dao.desconectar(); // desconecta
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
<a href ='index.jsp'>Voltar</a>
    <title>Refeições Cadastradas</title>
</head>
<body>
    <h1>Refeições Cadastradas</h1>

    <% if(mensagem != null) { %>
        <p style="color:green;"><%= mensagem %></p>
    <% } %>

    <!-- Formulário para cadastrar nova refeição -->
    <h2>Cadastrar Nova Refeição</h2>
    <form action="RefeicaoServlet" method="post">
        <label>Nome da Refeição: </label>
        <input type="text" name="nome" required /><br/><br/>

        <label>Ingredientes (separados por vírgula): </label>
        <input type="text" name="ingredientes" placeholder="ex: arroz, frango, sal" required /><br/><br/>

        <label>Quantidades (separadas por vírgula, na mesma ordem): </label>
        <input type="text" name="quantidades" placeholder="ex: 100, 200, 10" required /><br/><br/>

        <input type="submit" value="Cadastrar" />
    </form>

    <hr/>

    <!-- Tabela de refeições -->
    <table border="1">
        <tr>
            <th>Nome da Refeição</th>
        </tr>
        <% if (refeicoes != null) {
               for(String refeicao : refeicoes) { %>
        <tr>
            <td><%= refeicao %></td>
        </tr>
        <%   }
           } %>
    </table>
</body>
</html>
