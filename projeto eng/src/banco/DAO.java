package banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Duhorta
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Duhorta
 */
class conecta {
    private Connection conn;
    
    public Connection conectar(){
        try{
            Class.forName("org.postgresql.Driver");
            String url= "jdbc:postgresql://localhost:5432/postgres";
            conn = DriverManager.getConnection(url,"postgres","postgres");
            System.out.println("Banco de Dados conectado :)");
            return conn;
        }catch (ClassNotFoundException | SQLException e ){
            System.out.println("erro de conexão " + e.getMessage());
            return null;
            
        }
    }
    public void desconectar(){
        try{
            if(conn !=null && !conn.isClosed()){
                conn.close();
            }
        }catch(SQLException e){
            System.out.println("error ao desconectar");
        }
    }
}
public class DAO {
    private Connection conn;
    private PreparedStatement pstm;

    public void Conectar() {
        conecta conexao = new conecta();
        this.conn = conexao.conectar();
        if (conn == null) System.out.println("Erro na conexão");
        else System.out.println("Conexão realizada!");
    }

    public void Desconectar() {
        conecta conexao = new conecta();
        conexao.desconectar();
    }
public void adicionar_ingrediente(String ingrediente, int quantidade) {
    Conectar();
    String schema = "engProj";
    try {
        // Primeiro, verifica se o ingrediente já existe
        String sqlCheck = "SELECT quantidade FROM \"" + schema + "\".stock WHERE ingrediente = ?;";
        pstm = conn.prepareStatement(sqlCheck);
        pstm.setString(1, ingrediente);
        ResultSet rs = pstm.executeQuery();
        
        if (rs.next()) {
            // Se existe, atualiza a quantidade somando a nova
            int quantidadeAtual = rs.getInt("quantidade");
            String sqlUpdate = "UPDATE \"" + schema + "\".stock SET quantidade = ? WHERE ingrediente = ?;";
            pstm = conn.prepareStatement(sqlUpdate);
            pstm.setInt(1, quantidadeAtual + quantidade);
            pstm.setString(2, ingrediente);
            pstm.executeUpdate();
            System.out.println("Quantidade atualizada para o ingrediente: " + ingrediente);
        } else {
            // Se não existe, insere um novo registro
            String sqlInsert = "INSERT INTO \"" + schema + "\".stock (ingrediente, quantidade) VALUES (?, ?);";
            pstm = conn.prepareStatement(sqlInsert);
            pstm.setString(1, ingrediente);
            pstm.setInt(2, quantidade);
            pstm.executeUpdate();
            System.out.println("Novo ingrediente adicionado: " + ingrediente);
        }
    } catch (SQLException e) {
        System.out.println("Falha ao adicionar ingrediente: " + e.getMessage());
    }
    Desconectar();
}
    public void Cadastro_refeicao() {
        Conectar();
        Scanner scanner = new Scanner(System.in);
        System.out.println("coloque o nome da refeição:");
        String nome = scanner.nextLine();
        String schema = "engProj";
        ArrayList<String> ingredientes = new ArrayList<>();

        System.out.println("Digite os ingredientes (digite uma linha vazia para terminar):");
        int id = 0;
        while (true) {
            String ingrediente = scanner.nextLine();
            
            if (ingrediente.isEmpty()) {
                break;
            }
            System.out.println("quantos desse ingredientes há no estoque?");
            int quantidade= scanner.nextInt();
            adicionar_ingrediente(ingrediente, quantidade);
            ingredientes.add(ingrediente);
        }
        try {
            String sql1 = "INSERT INTO \"" + schema + "\".refeicoes(nome) VALUES(?);";
            pstm = conn.prepareStatement(sql1);
            pstm.setString(1, nome);
            pstm.execute();

            System.out.println("Refeição Cadastrada");

        } catch (SQLException e) {
            System.out.println("Falha ao inserir" + e.getMessage());
        }
        try {
            String sql2 = "SELECT id from \"" + schema + "\".refeicoes WHERE nome = ?;";
            pstm = conn.prepareStatement(sql2);
            pstm.setString(1, nome);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            for (int j = 0; j < ingredientes.size(); j++) {
                String sql3 = "INSERT INTO \"" + schema + "\".nutricional(id_refeicao,ingredientes) VALUES(?,?);";
                pstm = conn.prepareStatement(sql3);
                pstm.setInt(1, id);
                pstm.setString(2, ingredientes.get(j));
                pstm.execute();
            }
        } catch (SQLException e) {
            System.out.println("Falha ao inserir" + e.getMessage());
        }
        Desconectar();
    }

    public void cadastro_feedback() {
        Conectar();
        Scanner scanner = new Scanner(System.in);
        String schema = "engProj";
        System.out.println("qual das comidas voce quer comentar?");
        String nome_comida = scanner.nextLine();
        System.err.println("comentario:");
        String feedback = scanner.nextLine();
        int id = 0;
        try {
            pstm = conn.prepareStatement("SELECT id from \"" + schema + "\".refeicoes WHERE nome = ?;");
            pstm.setString(1, nome_comida);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            pstm = conn.prepareStatement("INSERT INTO \"" + schema + "\".feedback(id_refeicao, comentario) VALUES(?,?);");
            pstm.setInt(1, id);
            pstm.setString(2, feedback);
            pstm.execute();
        } catch (SQLException e) {
            System.out.println("Falha ao inserir" + e.getMessage());
        }
        Desconectar();
    }

    // Método para ver as refeições (listar refeições com seus ingredientes)
    public void ver_refeicoes() {
        Conectar();
        String schema = "engProj";
        try {
            String sql = "SELECT r.id, r.nome, n.ingredientes FROM \"" + schema + "\".refeicoes r LEFT JOIN \"" + schema + "\".nutricional n ON r.id = n.id_refeicao ORDER BY r.id;";
            pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            int currentId = -1;
            String nome = "";
            ArrayList<String> ingredientes = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                if (id != currentId) {
                    if (currentId != -1) {
                        System.out.println("Refeição: " + nome);
                        System.out.println("Ingredientes: " + String.join(", ", ingredientes));
                        System.out.println("---");
                    }
                    currentId = id;
                    nome = rs.getString("nome");
                    ingredientes.clear();
                }
                String ingrediente = rs.getString("ingredientes");
                if (ingrediente != null) {
                    ingredientes.add(ingrediente);
                }
            }
            if (currentId != -1) {
                System.out.println("Refeição: " + nome);
                System.out.println("Ingredientes: " + String.join(", ", ingredientes));
            }
        } catch (SQLException e) {
            System.out.println("Falha ao consultar" + e.getMessage());
        }
        Desconectar();
    }

    // Método para fazer um pedido (assumindo tabela orders com id, id_refeicao, status)
    public void fazer_pedido() {
        Conectar();
        Scanner scanner = new Scanner(System.in);
        String schema = "engProj";
        System.out.println("Qual refeição você deseja pedir?");
        String nome_refeicao = scanner.nextLine();
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
                Desconectar();
                return;
            }
            String sql2 = "INSERT INTO \"" + schema + "\".orders(id_refeicao, status) VALUES(?, 'ativo');";
            pstm = conn.prepareStatement(sql2);
            pstm.setInt(1, id_refeicao);
            pstm.execute();
            System.out.println("Pedido realizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Falha ao inserir pedido: " + e.getMessage());
        }
        Desconectar();
    }

    // Método para cancelar um pedido (assumindo tabela orders com id, status)
    public void cancelar_pedido() {
        Conectar();
        Scanner scanner = new Scanner(System.in);
        String schema = "engProj";
        System.out.println("Digite o ID do pedido a cancelar:");
        int id_pedido = scanner.nextInt();
        try {
            String sql = "UPDATE \"" + schema + "\".orders SET status = 'cancelado' WHERE id = ? AND status = 'ativo';";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id_pedido);
            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Pedido cancelado com sucesso!");
            } else {
                System.out.println("Pedido não encontrado ou já cancelado.");
            }
        } catch (SQLException e) {
            System.out.println("Falha ao cancelar pedido: " + e.getMessage());
        }
        Desconectar();
    }

    // Método para relatório de pedidos (contagem de pedidos por refeição)
    public void relatorio_pedidos() {
        Conectar();
        String schema = "engProj";
        try {
            String sql = "SELECT r.nome, COUNT(o.id) AS total_pedidos FROM \"" + schema + "\".refeicoes r LEFT JOIN \"" + schema + "\".orders o ON r.id = o.id_refeicao AND o.status = 'ativo' GROUP BY r.id, r.nome ORDER BY total_pedidos DESC;";
            pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            System.out.println("Relatório de Pedidos:");
            while (rs.next()) {
                String nome = rs.getString("nome");
                int total = rs.getInt("total_pedidos");
                System.out.println("Refeição: " + nome + " - Pedidos: " + total);
            }
        } catch (SQLException e) {
            System.out.println("Falha ao gerar relatório: " + e.getMessage());
        }
        Desconectar();
    }

    // Método para relatório de estoque (assumindo tabela stock com ingrediente, quantidade)
    public void relatorio_estoque() {
        Conectar();
        String schema = "engProj";
        try {
            String sql = "SELECT ingrediente, quantidade FROM \"" + schema + "\".stock ORDER BY ingrediente;";
            pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            System.out.println("Relatório de Estoque:");
            while (rs.next()) {
                String ingrediente = rs.getString("ingrediente");
                int quantidade = rs.getInt("quantidade");
                System.out.println("Ingrediente: " + ingrediente + " - Quantidade: " + quantidade);
            }
        } catch (SQLException e) {
            System.out.println("Falha ao gerar relatório: " + e.getMessage());
        }
        Desconectar();
    }


}