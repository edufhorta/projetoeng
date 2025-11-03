package banco;

import java.sql.*;
import java.util.*;

public class DAO {

    private Connection conn;
    private final String schema = "engProj";

    // Conectar ao banco
    public void conectar() {

    
        try {
            System.out.println("Tentando carregar driver PostgreSQL...");
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver carregado com sucesso.");

            String url = "jdbc:postgresql://localhost:5432/postgres";
            String usuario = "postgres";
            String senha = "postgres";

            System.out.println("Tentando conectar ao banco: " + url + " com usuário: " + usuario);
            conn = DriverManager.getConnection(url, usuario, senha);

            if (conn != null && !conn.isClosed()) {
                System.out.println("Banco de Dados conectado com sucesso!"); 
            } else {
                System.out.println("Falha ao conectar: conexão é null ou fechada.");
                throw new RuntimeException("Conexão é null ou fechada");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC não encontrado: " + e.getMessage());
            throw new RuntimeException("Driver JDBC não encontrado", e);
        } catch (SQLException e) {
            System.out.println("Erro de conexão SQL: " + e.getMessage());
            throw new RuntimeException("Falha na conexão com o banco de dados", e);
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            throw new RuntimeException("Erro inesperado ao conectar", e);
        }
    }

    public void desconectar() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("Banco de dados desconectado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao desconectar: " + e.getMessage());
        }
    }
    // Adicionar ingrediente ao estoque
    public void adicionar_ingrediente(String ingrediente, int quantidade) throws SQLException {
        String sqlCheck = "SELECT quantidade FROM \"" + schema + "\".stock WHERE ingrediente = ?;";
        PreparedStatement pstm = conn.prepareStatement(sqlCheck);
        pstm.setString(1, ingrediente);
        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            int atual = rs.getInt("quantidade");
            String sqlUpdate = "UPDATE \"" + schema + "\".stock SET quantidade = ? WHERE ingrediente = ?;";
            PreparedStatement updatePstm = conn.prepareStatement(sqlUpdate);
            updatePstm.setInt(1, atual + quantidade);
            updatePstm.setString(2, ingrediente);
            updatePstm.executeUpdate();
            updatePstm.close();
        } else {
            String sqlInsert = "INSERT INTO \"" + schema + "\".stock (ingrediente, quantidade) VALUES (?, ?);";
            PreparedStatement insertPstm = conn.prepareStatement(sqlInsert);
            insertPstm.setString(1, ingrediente);
            insertPstm.setInt(2, quantidade);
            insertPstm.executeUpdate();
            insertPstm.close();
        }

        rs.close();
        pstm.close();
    }

    // Cadastrar refeição
    public void cadastro_refeicao(String nome, List<String> ingredientes, List<Integer> quantidades) throws SQLException {
        String sql1 = "INSERT INTO \"" + schema + "\".refeicoes(nome) VALUES(?);";
        PreparedStatement pstm = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
        pstm.setString(1, nome);
        pstm.executeUpdate();

        ResultSet rs = pstm.getGeneratedKeys();
        int id = 0;
        if (rs.next()) id = rs.getInt(1);

        rs.close();
        pstm.close();

        for (int i = 0; i < ingredientes.size(); i++) {
            adicionar_ingrediente(ingredientes.get(i), quantidades.get(i));

            String sql2 = "INSERT INTO \"" + schema + "\".nutricional(id_refeicao, ingredientes) VALUES(?,?);";
            PreparedStatement pstm2 = conn.prepareStatement(sql2);
            pstm2.setInt(1, id);
            pstm2.setString(2, ingredientes.get(i));
            pstm2.executeUpdate();
            pstm2.close();
        }
    }

    // Fazer pedido
    public void fazer_pedido(String nome_refeicao) throws SQLException {
        String sql1 = "SELECT id FROM \"" + schema + "\".refeicoes WHERE nome = ?;";
        PreparedStatement pstm = conn.prepareStatement(sql1);
        pstm.setString(1, nome_refeicao);
        ResultSet rs = pstm.executeQuery();

        int id_refeicao = 0;
        if (rs.next()) {
            id_refeicao = rs.getInt("id");
        } else {
            System.out.println("Refeição não encontrada.");
            rs.close();
            pstm.close();
            return;
        }

        rs.close();
        pstm.close();

        String sql2 = "INSERT INTO \"" + schema + "\".orders(id_refeicao, status) VALUES(?, 'ativo');";
        PreparedStatement pstm2 = conn.prepareStatement(sql2);
        pstm2.setInt(1, id_refeicao);
        pstm2.executeUpdate();
        pstm2.close();
    }

    // Cadastrar feedback
    public void cadastro_feedback(String nome_refeicao, String comentario) throws SQLException {
        String sql1 = "SELECT id FROM \"" + schema + "\".refeicoes WHERE nome = ?;";
        PreparedStatement pstm = conn.prepareStatement(sql1);
        pstm.setString(1, nome_refeicao);
        ResultSet rs = pstm.executeQuery();

        int id_refeicao = 0;
        if (rs.next()) id_refeicao = rs.getInt("id");

        rs.close();
        pstm.close();

        String sql2 = "INSERT INTO \"" + schema + "\".feedback(id_refeicao, comentario) VALUES(?, ?);";
        PreparedStatement pstm2 = conn.prepareStatement(sql2);
        pstm2.setInt(1, id_refeicao);
        pstm2.setString(2, comentario);
        pstm2.executeUpdate();
        pstm2.close();
    }

    // Listar refeições
    public List<String> listar_refeicoes() throws SQLException {
        List<String> refeicoes = new ArrayList<>();
        String sql = "SELECT nome FROM \"" + schema + "\".refeicoes ORDER BY id;";
        PreparedStatement pstm = conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();

        while (rs.next()) {
            refeicoes.add(rs.getString("nome"));
        }

        rs.close();
        pstm.close();
        return refeicoes;
    }

    // Listar estoque
    public Map<String, Integer> listar_estoque() throws SQLException {
        Map<String, Integer> estoque = new HashMap<>();
        String sql = "SELECT ingrediente, quantidade FROM \"" + schema + "\".stock ORDER BY ingrediente;";
        PreparedStatement pstm = conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();

        while (rs.next()) {
            estoque.put(rs.getString("ingrediente"), rs.getInt("quantidade"));
        }

        rs.close();
        pstm.close();
        return estoque;
    }
}
