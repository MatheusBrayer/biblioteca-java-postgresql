import java.util.Scanner;
public class Main {
    private static final String[] CONSULTAS = {
        "SELECT codigo,titulo,tipo,disponivel FROM item ORDER BY codigo",
        "SELECT u.nome AS usuario,i.titulo AS item,e.data_retirada,e.data_devolucao_prevista FROM emprestimo e JOIN usuario u ON u.id=e.usuario_id JOIN item i ON i.id=e.item_id WHERE e.data_devolucao IS NULL",
        "SELECT u.nome,COALESCE(SUM(e.valor_multa),0) AS total_multas FROM usuario u LEFT JOIN emprestimo e ON e.usuario_id=u.id GROUP BY u.id,u.nome ORDER BY u.nome",
        "SELECT i.codigo,i.titulo FROM item i LEFT JOIN emprestimo e ON e.item_id=i.id WHERE e.id IS NULL ORDER BY i.codigo"
    };
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in); BibliotecaDAO dao=new BibliotecaDAO();
        while(true) {
            System.out.println("\n=== BIBLIOTECA ===\n1 - Cadastrar item\n2 - Cadastrar usuario\n3 - Listar acervo (Java + SQL)\n4 - Emprestar\n5 - Devolver\n6 - Consulta SQL: acervo\n7 - Consulta SQL: emprestimos abertos\n8 - Consulta SQL: multas por usuario\n9 - Consulta SQL: nunca emprestados\n10 - Teste POO com arrays (exercicio 1)\n0 - Sair");
            System.out.print("Opcao: "); String opcao=sc.nextLine().trim();
            if(opcao.equals("0"))break;
            try {
                switch(opcao) {
                    case "1" -> {System.out.print("Codigo numerico (ex.: 101): ");int codigo=Integer.parseInt(sc.nextLine());System.out.print("Titulo: ");String titulo=sc.nextLine();System.out.print("Tipo (livro/revista): ");String tipo=sc.nextLine().trim().toLowerCase();System.out.print("Autor: ");String autor=sc.nextLine();System.out.print("Edicao: ");String edicao=sc.nextLine();dao.cadastrarItem(codigo,titulo,tipo,autor,edicao);System.out.println("Cadastrado!");}
                    case "2" -> {System.out.print("Nome: ");String nome=sc.nextLine();System.out.print("Tipo (aluno/professor): ");String tipo=sc.nextLine().trim().toLowerCase();dao.cadastrarUsuario(nome,tipo);System.out.println("Cadastrado!");}
                    case "3" -> dao.listarAcervo();
                    case "4" -> {System.out.print("Codigo do item (ex.: L001): ");String codigo=sc.nextLine();System.out.print("ID do usuario: ");int id=Integer.parseInt(sc.nextLine());dao.emprestar(codigo,id);}
                    case "5" -> {System.out.print("Codigo do item: ");dao.devolver(sc.nextLine());}
                    case "6","7","8","9" -> dao.consultar(CONSULTAS[Integer.parseInt(opcao)-6]);
                    case "10" -> testePOO();
                    default -> System.out.println("Opcao invalida");
                }
            }catch(Exception e){System.out.println("Erro: "+e.getMessage());}
        }
    }
    private static void testePOO(){
        Biblioteca b=new Biblioteca(6);b.cadastrar(new Livro(1,"Dom Casmurro"));b.cadastrar(new Livro(2,"O Cortico"));b.cadastrar(new Revista(3,"Ciencia Hoje"));b.cadastrar(new Revista(4,"Superinteressante"));
        Aluno a=new Aluno("Ana");Professor p=new Professor("Carlos");
        b.listarAcervo();b.emprestar(1,a);b.emprestar(2,a);b.emprestar(3,a);b.emprestar(4,a); // recusado por limite
        b.emprestar(4,p);b.devolver(3,2);b.listarAcervo();
    }
}
