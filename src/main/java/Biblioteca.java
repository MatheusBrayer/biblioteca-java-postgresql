public class Biblioteca {
    private final ItemBiblioteca[] acervo;
    private final Usuario[] responsaveis;
    private int total;
    public Biblioteca(int capacidade) {
        acervo = new ItemBiblioteca[capacidade];
        responsaveis = new Usuario[capacidade];
    }
    public void cadastrar(ItemBiblioteca item) {
        if (total >= acervo.length) throw new IllegalStateException("Acervo cheio");
        for (int i = 0; i < total; i++)
            if (acervo[i].getCodigo() == item.getCodigo()) throw new IllegalArgumentException("Codigo duplicado");
        acervo[total++] = item;
    }
    private int indice(int codigo) {
        for (int i = 0; i < total; i++) if (acervo[i].getCodigo() == codigo) return i;
        return -1;
    }
    public boolean emprestar(int codigo, Usuario usuario) {
        int i = indice(codigo);
        if (i < 0 || !acervo[i].isDisponivel() || !usuario.podeEmprestar()) {
            System.out.println("Emprestimo recusado: " + codigo + " para " + usuario.getNome());
            return false;
        }
        acervo[i].emprestar();
        responsaveis[i] = usuario;
        usuario.registrarEmprestimo();
        System.out.println("Emprestimo realizado: " + acervo[i].getTitulo() + " para " + usuario.getNome());
        return true;
    }
    public boolean devolver(int codigo, int diasAtraso) {
        int i = indice(codigo);
        if (i < 0 || acervo[i].isDisponivel()) return false;
        double multa = acervo[i].calcularMulta(diasAtraso);
        responsaveis[i].registrarDevolucao();
        responsaveis[i] = null;
        acervo[i].devolver();
        System.out.printf("Devolucao: %s | Multa: R$ %.2f%n", acervo[i].getTitulo(), multa);
        return true;
    }
    public void listarAcervo() {
        for (int i = 0; i < total; i++) System.out.println(acervo[i]);
    }
}
