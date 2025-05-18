package Compilador.Lexico;

public class Token {
    private final String tipo;
    private final String lexema;
    private int linha;
    private int coluna;

    public Token(String tipo, String lexema) {
        this(tipo, lexema, -1, -1); // -1 indica posição não definida
    }

    public Token(String tipo, String lexema, int linha, int coluna) {
        if (tipo == null || lexema == null) {
            throw new IllegalArgumentException("Tipo e Lexema não podem ser nulos.");
        }
        if (tipo.isEmpty() || lexema.isEmpty()) {
            throw new IllegalArgumentException("Tipo e Lexema não podem ser vazios.");
        }
        this.tipo = tipo;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }

    // Getters
    public String getTipo() { return tipo; }
    public String getLexema() { return lexema; }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }

    // Setters para posição (imutáveis para outros atributos)
    public Token setPosicao(int linha, int coluna) {
    this.linha = linha;
    this.coluna = coluna;
    return this;  // Retorna a própria instância
    }

    // Método utilitário
    public boolean isTipo(String tipo) {
        return this.tipo.equals(tipo);
    }

    @Override
    public String toString() {
        if (linha == -1 || coluna == -1) {
            return String.format("<%s, '%s'>", tipo, lexema);
        }
        return String.format("<%s, '%s' [%d:%d]>", tipo, lexema, linha, coluna);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        return tipo.equals(other.tipo) && lexema.equals(other.lexema);
    }

    @Override
    public int hashCode() {
        return 31 * tipo.hashCode() + lexema.hashCode();
    }
}