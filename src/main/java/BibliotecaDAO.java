import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BibliotecaDAO {
    public void cadastrarItem(int codigo, String titulo, String tipo, String autor, String edicao) throws SQLException {
        if (!tipo.equals("livro") && !tipo.equals("revista")) throw new IllegalArgumentException("Tipo invalido");
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(
                "INSERT INTO item(codigo,titulo,tipo,autor,edicao) VALUES(?,?,?,?,?)")) {
            p.setString(1, String.valueOf(codigo)); p.setString(2, titulo); p.setString(3, tipo);
            p.setString(4, autor); p.setString(5, edicao); p.executeUpdate();
        }
    }
    public void cadastrarUsuario(String nome, String tipo) throws SQLException {
        int limite = switch (tipo) { case "aluno" -> new Aluno(nome).getLimiteItens(); case "professor" -> new Professor(nome).getLimiteItens(); default -> throw new IllegalArgumentException("Tipo invalido"); };
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(
                "INSERT INTO usuario(nome,tipo,limite_itens) VALUES(?,?,?)")) {
            p.setString(1,nome); p.setString(2,tipo); p.setInt(3,limite); p.executeUpdate();
        }
    }
    public void listarAcervo() throws SQLException {
        // Um unico laco, sem verificar o tipo durante a listagem (polimorfismo).
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("SELECT codigo,titulo,tipo,disponivel FROM item ORDER BY id"); ResultSet r = p.executeQuery()) {
            ItemBiblioteca[] itens = new ItemBiblioteca[1000]; int total=0;
            while(r.next()) {
                if(total == itens.length) throw new IllegalStateException("Capacidade de exibicao excedida");
                int codigo = Integer.parseInt(r.getString("codigo").replaceAll("\\D", ""));
                ItemBiblioteca item = r.getString("tipo").equals("livro") ? new Livro(codigo,r.getString("titulo")) : new Revista(codigo,r.getString("titulo"));
                if(!r.getBoolean("disponivel")) item.emprestar();
                itens[total++] = item;
            }
            for(int i=0;i<total;i++) System.out.println(itens[i]);
        }
    }
    public void emprestar(String codigo, int usuarioId) throws SQLException {
        try(Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                int itemId; String tipo; String titulo;
                try(PreparedStatement p=c.prepareStatement("SELECT id,tipo,titulo,disponivel FROM item WHERE codigo=? FOR UPDATE")) {
                    p.setString(1,codigo);
                    try(ResultSet r=p.executeQuery()) {
                        if(!r.next()) throw new IllegalArgumentException("Item nao encontrado");
                        if(!r.getBoolean("disponivel")) throw new IllegalStateException("Item indisponivel");
                        itemId=r.getInt("id"); tipo=r.getString("tipo"); titulo=r.getString("titulo");
                    }
                }
                // Bloqueia o usuario para serializar emprestimos concorrentes do mesmo usuario.
                int limite;
                try(PreparedStatement p=c.prepareStatement("SELECT limite_itens FROM usuario WHERE id=? FOR UPDATE")) {
                    p.setInt(1,usuarioId);
                    try(ResultSet r=p.executeQuery()) { if(!r.next()) throw new IllegalArgumentException("Usuario nao encontrado"); limite=r.getInt(1); }
                }
                int ativos;
                try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM emprestimo WHERE usuario_id=? AND data_devolucao IS NULL")) {
                    p.setInt(1,usuarioId); try(ResultSet r=p.executeQuery()) {r.next(); ativos=r.getInt(1);}
                }
                if(ativos>=limite) throw new IllegalStateException("Limite de emprestimos atingido");
                ItemBiblioteca item=tipo.equals("livro")?new Livro(0,titulo):new Revista(0,titulo);
                LocalDate hoje=LocalDate.now();
                try(PreparedStatement p=c.prepareStatement("INSERT INTO emprestimo(item_id,usuario_id,data_retirada,data_devolucao_prevista) VALUES(?,?,?,?)")) {
                    p.setInt(1,itemId); p.setInt(2,usuarioId); p.setDate(3,Date.valueOf(hoje)); p.setDate(4,Date.valueOf(hoje.plusDays(item.getPrazoDias()))); p.executeUpdate();
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE item SET disponivel=false WHERE id=?")) {p.setInt(1,itemId);p.executeUpdate();}
                c.commit(); System.out.println("Emprestimo realizado: "+titulo);
            } catch(SQLException|RuntimeException e) {c.rollback();throw e;}
        }
    }
    public void devolver(String codigo) throws SQLException {
        try(Connection c=Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                int itemId; String tipo; String titulo;
                try(PreparedStatement p=c.prepareStatement("SELECT id,tipo,titulo FROM item WHERE codigo=? FOR UPDATE")) {
                    p.setString(1,codigo); try(ResultSet r=p.executeQuery()) {if(!r.next())throw new IllegalArgumentException("Item nao encontrado");itemId=r.getInt(1);tipo=r.getString(2);titulo=r.getString(3);}
                }
                int emprestimoId; LocalDate prevista;
                try(PreparedStatement p=c.prepareStatement("SELECT id,data_devolucao_prevista FROM emprestimo WHERE item_id=? AND data_devolucao IS NULL FOR UPDATE")) {
                    p.setInt(1,itemId);try(ResultSet r=p.executeQuery()) {if(!r.next())throw new IllegalStateException("Item nao esta emprestado");emprestimoId=r.getInt(1);prevista=r.getDate(2).toLocalDate();}
                }
                ItemBiblioteca item=tipo.equals("livro")?new Livro(0,titulo):new Revista(0,titulo);
                int atraso=(int)Math.max(0,ChronoUnit.DAYS.between(prevista,LocalDate.now()));
                double multa=item.calcularMulta(atraso);
                try(PreparedStatement p=c.prepareStatement("UPDATE emprestimo SET data_devolucao=?,valor_multa=? WHERE id=?")) {p.setDate(1,Date.valueOf(LocalDate.now()));p.setDouble(2,multa);p.setInt(3,emprestimoId);p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("UPDATE item SET disponivel=true WHERE id=?")) {p.setInt(1,itemId);p.executeUpdate();}
                c.commit();System.out.printf("Devolvido: %s | atraso: %d dias | multa: R$ %.2f%n",titulo,atraso,multa);
            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }
    }
    public void consultar(String sql) throws SQLException {
        try(Connection c=Conexao.abrir();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)) {
            ResultSetMetaData m=r.getMetaData(); int n=m.getColumnCount();
            for(int i=1;i<=n;i++)System.out.print(m.getColumnLabel(i)+(i==n?"\n":" | "));
            while(r.next()){for(int i=1;i<=n;i++)System.out.print(r.getString(i)+(i==n?"\n":" | "));}
        }
    }
}
