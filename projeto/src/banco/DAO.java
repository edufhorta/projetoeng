package banco;

import java.sql.*;
import java.util.*;

class Conecta {
    private Connection conn;

    public Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            conn = DriverManager.getConnection(url, "postgres", "postgres");
            System.out.println("Banco de Dados conectado :)");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
            return null;
        }
    }

    public void desconectar() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao desconectar: " + e.getMessage());
        }
    }
}

public class DAO {
    private Connection conn;
    private PreparedStatement pstm;
    private final String schema = "engProj";

    public void conectar() {
        Conecta conexao = new Conecta();
        this.conn = conexao.conectar();
        if (conn == null) System.out.println("Erro na conexão");
    }

    public void desconectar() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    // Adicionar ingrediente ao estoque
    public void adicionar_ingrediente(String ingrediente, int quantidade) {
        conectar();
        try {
            String sqlCheck = "SELECT quantidade FROM \"" + schema + "\".stock WHERE ingrediente = ?;";
            pstm = conn.prepareStatement(sqlCheck);
            pstm.setString(1, ingrediente);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                int atual = rs.getInt("quantidade");
                String sqlUpdate = "UPDATE \"" + schema + "\".stock SET quantidade = ? WHERE ingrediente = ?;";
                pstm = conn.prepareStatement(sqlUpdate);
                pstm.setInt(1, atual + quantidade);
                pstm.setString(2, ingrediente);
                pstm.executeUpdate();
            } else {
                String sqlInsert = "INSERT INTO \"" + schema + "\".stock (ingrediente, quantidade) VALUES (?, ?);";
                pstm = conn.prepareStatement(sqlInsert);
                pstm.setString(1, ingrediente);
                pstm.setInt(2, quantidade);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Falha ao adicionar ingrediente: " + e.getMessage());
        }
        desconectar();
    }

    // Cadastrar refeição (web)
    public void Cadastro_refeicao(String nome, List<String> ingredientes, List<Integer> quantidades) {
        conectar();
        int id = 0;
        try {
            String sql1 = "INSERT INTO \"" + schema + "\".refeicoes(nome) VALUES(?);";
            pstm = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, nome);
            pstm.executeUpdate();

            ResultSet rs = pstm.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);

            for (int i = 0; i < ingredientes.size(); i++) {
                adicionar_ingrediente(ingredientes.get(i), quantidades.get(i));
                String sql2 = "INSERT INTO \"" + schema + "\".nutricional(id_refeicao, ingredientes) VALUES(?,?);";
                pstm = conn.prepareStatement(sql2);
                pstm.setInt(1, id);
                pstm.setString(2, ingredientes.get(i));
                pstm.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Falha ao cadastrar refeição: " + e.getMessage());
        }
        desconectar();
    }

    // Fazer pedido (recebendo nome da refeição)
    public void fazer_pedido(String nome_refeicao) {
        conectar();
        int id_refeicao = 0;
        try {
            String sql1 = "SELECT id FROM \"" + schema + "\".refeicoes WHERE nome = ?;";
            pstm = conn.prepareStatement(sql1);
            pstm.setString(1, nome_refeicao);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                id_refeicao = rs.getInt("id");
            } else {
                System.out.println("Refeição não encontrada.");
                desconectar();
                return;
            }

            String sql2 = "INSERT INTO \"" + schema + "\".orders(id_refeicao, status) VALUES(?, 'ativo');";
            pstm = conn.prepareStatement(sql2);
            pstm.setInt(1, id_refeicao);
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Falha ao fazer pedido: " + e.getMessage());
        }
        desconectar();
    }

    // Cadastrar feedback (recebendo nome da refeição)
    public void cadastro_feedback(String nome_refeicao, String comentario) {
        conectar();
        int id_refeicao = 0;
        try {
            String sql1 = "SELECT id FROM \"" + schema + "\".refeicoes WHERE nome = ?;";
            pstm = conn.prepareStatement(sql1);
            pstm.setString(1, nome_refeicao);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) id_refeicao = rs.getInt("id");

            String sql2 = "INSERT INTO \"" + schema + "\".feedback(id_refeicao, comentario) VALUES(?, ?);";
            pstm = conn.prepareStatement(sql2);
            pstm.setInt(1, id_refeicao);
            pstm.setString(2, comentario);
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Falha ao cadastrar feedback: " + e.getMessage());
        }
        desconectar();
    }

    // Listar refeições (para JSP)
    public List<String> listar_refeicoes() {
        conectar();
        List<String> refeicoes = new ArrayList<>();
        try {
            String sql = "SELECT nome FROM \"" + schema + "\".refeicoes ORDER BY id;";
            pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                refeicoes.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            System.out.println("Falha ao listar refeições: " + e.getMessage());
        }
        desconectar();
        return refeicoes;
    }

    // Listar estoque (para JSP)
    public Map<String, Integer> listar_estoque() {
        conectar();
        Map<String, Integer> estoque = new HashMap<>();
        try {
            String sql = "SELECT ingrediente, quantidade FROM \"" + schema + "\".stock ORDER BY ingrediente;";
            pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                estoque.put(rs.getString("ingrediente"), rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            System.out.println("Falha ao listar estoque: " + e.getMessage());
        }
        desconectar();
        return estoque;
    }
}
