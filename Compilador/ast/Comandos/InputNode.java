package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class InputNode extends ASTNode {
    public final ASTNode variavel;

    public InputNode(ASTNode variavel) {
        this.variavel = variavel;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Cabeçalho do Input
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Input\n");
        
        String childIndent = indent + (isLast ? "    " : "│   ");
        
        // Variável
        sb.append(childIndent).append("└── Variable: \n")
          .append(variavel.toFormattedString(childIndent + "    ", true));
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}