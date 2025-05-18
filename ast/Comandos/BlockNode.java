package Compilador.ast.Comandos;

import Compilador.ast.*;
import java.util.List;

public class BlockNode extends ASTNode {
    private final List<ASTNode> statements;

    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Block\n");
        String childIndent = indent + (isLast ? "    " : "│   ");

        for (int i = 0; i < statements.size(); i++) {
            boolean isLastStatement = (i == statements.size() - 1);
            sb.append(statements.get(i).toFormattedString(childIndent, isLastStatement));
            if (!isLastStatement) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}