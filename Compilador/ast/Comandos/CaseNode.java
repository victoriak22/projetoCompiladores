package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class CaseNode extends ASTNode {
    public final ASTNode valor;
    public final ASTNode corpo;

    public CaseNode(ASTNode valor, ASTNode corpo) {
        this.valor = valor;
        this.corpo = corpo;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Cabeçalho do "Case"
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Case\n");

        String childIndent = indent + (isLast ? "" : "│");

        // Valor (primeiro filho)
        sb.append(childIndent).append("├── Value:\n")
          .append(valor.toFormattedString(childIndent + "│   ", false));  // Indentação do valor

        // Corpo (último filho)
        sb.append(childIndent).append("└── Body:\n")
          .append(corpo.toFormattedString(childIndent + "    ", true));  // Indentação do corpo

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);  // Consistência com outras classes
    }
}
