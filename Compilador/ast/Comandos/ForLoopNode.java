package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class ForLoopNode extends ASTNode {
    public final ASTNode inicializacao;
    public final ASTNode condicao;
    public final ASTNode incremento;
    public final BlockNode corpo;

    public ForLoopNode(ASTNode inicializacao, ASTNode condicao,
                       ASTNode incremento, BlockNode corpo) {
        this.inicializacao = inicializacao;
        this.condicao = condicao;
        this.incremento = incremento;
        this.corpo = corpo;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        // ForLoop header
        sb.append(indent).append(isLast ? "└── " : "├── ").append("ForLoop\n");

        String childIndent = indent + (isLast ? "    " : "│   ");

        // Append each part of the loop
        appendLoopPart(sb, childIndent, "Initialization:", inicializacao, false);
        appendLoopPart(sb, childIndent, "Condition:", condicao, false);
        appendLoopPart(sb, childIndent, "Increment:", incremento, false);
        appendLoopPart(sb, childIndent, "Body:", corpo, true);

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true); // Consistent with other nodes
    }

    private void appendLoopPart(StringBuilder sb, String indent, String label, ASTNode part, boolean isLast) {
        sb.append(indent).append(isLast ? "└── " : "├── ").append(label).append("\n")
          .append(part.toFormattedString(indent + (isLast ? "    " : "│   "), isLast));
    }
}
