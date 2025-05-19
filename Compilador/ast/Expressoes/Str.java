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
        return toFormattedString("", true); // Usa a versão formatada com indentação inicial vazia
    }

    // Método acessador
    public String getValor() {
        return valor;
    }
}
