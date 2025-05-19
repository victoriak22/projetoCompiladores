package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class ElseIfNode extends ASTNode {
    public final ASTNode condicao;
    public final BlockNode corpo;

    public ElseIfNode(ASTNode condicao, BlockNode corpo) {
        this.condicao = condicao;
        this.corpo = corpo;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        // Cabeçalho do ElseIf
        sb.append(indent).append(isLast ? "└── " : "├── ").append("ElseIf\n");

        String childIndent = indent + (isLast ? "    " : "│   ");

        // Condição
        sb.append(childIndent).append("├── ").append("Condition:\n");
        sb.append(condicao.toFormattedString(childIndent + "│   ", false));

        // Corpo
        sb.append(childIndent).append("└── ").append("Body:\n");
        sb.append(corpo.toFormattedString(childIndent + "    ", true));

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}
