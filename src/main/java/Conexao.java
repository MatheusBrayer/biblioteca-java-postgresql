import java.sql.*;
public final class Conexao {
    private Conexao() {}
    public static Connection abrir() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/biblioteca");
        String usuario = System.getenv().getOrDefault("DB_USER", "postgres");
        String senha = System.getenv().getOrDefault("DB_PASSWORD", "");
        return DriverManager.getConnection(url, usuario, senha);
    }
}
