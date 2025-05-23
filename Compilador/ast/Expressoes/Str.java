package Compilador.ast.Expressoes;

import Compilador.ast.ASTNode;

public class Str extends ASTNode {
    private final String valor;

    public Str(String valor) {
        this.valor = valor;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        return indent + (isLast ? "└── " : "├── ") + "'" + valor + "'\n";
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }

    public String getValor() {
        return valor;
    }
}
