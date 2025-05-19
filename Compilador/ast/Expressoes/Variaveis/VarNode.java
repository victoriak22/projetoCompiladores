package Compilador.ast.Expressoes.Variaveis;

import Compilador.ast.ASTNode;

public class VarNode extends ASTNode {
    private final String name;

    public VarNode(String name) {
        this.name = name;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        return indent + (isLast ? "└── " : "├── ") + name + "\n";
    }

    @Override
    public String toString() {
        return toFormattedString("", true); // Usa versão formatada para consistência
    }

    // Método de acesso
    public String getName() {
        return name;
    }
}
