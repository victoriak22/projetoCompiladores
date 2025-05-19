package Compilador.ast.Expressoes;

import Compilador.ast.ASTNode;

public class Num extends ASTNode {
    public enum Tipo { INTEGER, DECIMAL }
    
    private final String valor;
    private final Tipo tipo;

    public Num(String valor, Tipo tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public Num(String valor, String tipoStr) {
        this.valor = valor;
        this.tipo = Tipo.valueOf(tipoStr.toUpperCase());
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        return indent + (isLast ? "└── " : "├── ") + valor + "\n";
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }

    public String getValor() {
        return valor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public boolean isInteger() {
        return tipo == Tipo.INTEGER;
    }

    public boolean isDecimal() {
        return tipo == Tipo.DECIMAL;
    }
}
