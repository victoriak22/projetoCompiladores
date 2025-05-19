package Compilador.ast.Comandos;

import Compilador.ast.*;

public class AssignNode extends ASTNode {
    private final ASTNode lhs;
    private final ASTNode rhs;

    public AssignNode(ASTNode lhs, ASTNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        // Cabeçalho do nó Assign
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Assign\n");
        String childIndent = indent + (isLast ? "    " : "│   ");
        
        // Subnó LHS (left-hand side)
        sb.append(childIndent).append("├── LHS:\n");
        sb.append(lhs.toFormattedString(childIndent + "│   ", false));
        
        // Subnó RHS (right-hand side)
        sb.append(childIndent).append("└── RHS:\n");
        sb.append(rhs.toFormattedString(childIndent + "    ", true));
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}
