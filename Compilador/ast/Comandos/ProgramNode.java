package Compilador.ast.Comandos;

import java.util.List;
import Compilador.ast.*;

public class ProgramNode extends ASTNode {
    private final List<ASTNode> statements;

    public ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Program\n");

        String childIndent = indent + (isLast ? "    " : "│   ");
        for (int i = 0; i < statements.size(); i++) {
            boolean isLastChild = (i == statements.size() - 1);
            sb.append(statements.get(i).toFormattedString(childIndent, isLastChild));
        }

        return sb.toString();
    }

    public List<ASTNode> getStatements() {
        return statements;
    }
}
