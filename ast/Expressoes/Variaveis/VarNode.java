package Compilador.ast.Expressoes.Variaveis;
import Compilador.ast.ASTNode;

// Variável (ex: :x)
public class VarNode extends ASTNode {
    public String name;

    public VarNode(String name) {
        this.name = name;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Var(" + name + ")\n";
    }
}
