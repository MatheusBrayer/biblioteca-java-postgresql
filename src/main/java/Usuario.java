public abstract class Usuario {
    private final String nome;
    private int quantidadeEmprestada;
    public Usuario(String nome) { this.nome = nome; }
    public abstract int getLimiteItens();
    public String getNome() { return nome; }
    public int getQuantidadeEmprestada() { return quantidadeEmprestada; }
    public boolean podeEmprestar() { return quantidadeEmprestada < getLimiteItens(); }
    void registrarEmprestimo() { quantidadeEmprestada++; }
    void registrarDevolucao() { if (quantidadeEmprestada > 0) quantidadeEmprestada--; }
}
