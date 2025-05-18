package Compilador.ast.Comandos;

import Compilador.ast.*;

public class ReturnNode extends ASTNode {
    private final ASTNode expression;

    public ReturnNode(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Return\n");
        if (expression != null) {
            sb.append(indent).append("    ├── Expression:\n")
              .append(expression.toFormattedString(indent + "│   ", true));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}