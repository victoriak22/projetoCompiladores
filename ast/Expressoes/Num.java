// Num.java
package Compilador.ast.Expressoes;

import Compilador.ast.ASTNode;

public class Num extends ASTNode {
    public enum Tipo { INTEGER, DECIMAL }
    
    public final String valor;
    public final Tipo tipo;

    // Construtor principal
    public Num(String valor, Tipo tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    // Construtor alternativo (para usar string)
    public Num(String valor, String tipoStr) {
        this.valor = valor;
        this.tipo = Tipo.valueOf(tipoStr.toUpperCase());
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Num(" + valor + " [" + tipo + "])\n";
    }
}