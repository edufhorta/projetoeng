package projeto.eng;

import banco.DAO;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class ProjetoEng {
    public static void main(String[] args) {
        DAO dao = new DAO();
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8); // Atualizado para UTF-8
        boolean running = true;

        while (running) {
            System.out.println("\n=== Menu do Sistema de Refeições ===");
            System.out.println("1. Cadastrar Refeição");
            System.out.println("2. Cadastrar Feedback");
            System.out.println("3. Ver Refeições");
            System.out.println("4. Fazer Pedido");
            System.out.println("5. Cancelar Pedido");
            System.out.println("6. Relatório de Pedidos");
            System.out.println("7. Relatório de Estoque");
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (opcao) {
                case 1:
                    dao.Cadastro_refeicao();
                    break;
                case 2:
                    dao.cadastro_feedback();
                    break;
                case 3:
                    dao.ver_refeicoes();
                    break;
                case 4:
                    dao.fazer_pedido();
                    break;
                case 5:
                    dao.cancelar_pedido();
                    break;
                case 6:
                    dao.relatorio_pedidos();
                    break;
                case 7:
                    dao.relatorio_estoque();
                    break;
               
                case 8:
                    running = false;
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }
}
