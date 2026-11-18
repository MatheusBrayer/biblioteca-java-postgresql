public abstract class ItemBiblioteca {
    private final int codigo;
    private final String titulo;
    private boolean disponivel = true;

    public ItemBiblioteca(int codigo, String titulo) {
        this.codigo = codigo;
        this.titulo = titulo;
    }
    public abstract int getPrazoDias();
    public abstract double calcularMulta(int diasAtraso);
    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public boolean isDisponivel() { return disponivel; }
    void emprestar() { disponivel = false; }
    void devolver() { disponivel = true; }
    @Override public String toString() {
        return codigo + " - " + titulo + " | Prazo: " + getPrazoDias() +
               " dias | Multa/dia: R$ " + String.format("%.2f", calcularMulta(1)) +
               " | Disponivel: " + (disponivel ? "sim" : "nao");
    }
}
