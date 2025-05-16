package Compilador.ast.Expressoes;
import Compilador.ast.ASTNode;

public class Str extends ASTNode {
    public String value;

    public Str(String value) {
        this.value = value;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Str(" + value + ")\n";
    }
}
