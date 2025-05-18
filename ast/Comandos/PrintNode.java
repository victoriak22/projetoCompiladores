package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class PrintNode extends ASTNode {
    public final ASTNode conteudo;
    private static final String CONNECTOR = "│   ";  // Definindo a constante CONNECTOR

    public PrintNode(ASTNode conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Cabeçalho do Print
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Print\n");

        // Verifica se o conteúdo não é nulo e formata adequadamente
        if (conteudo != null) {
            sb.append(indent).append(CONNECTOR).append("Content:\n")
              .append(conteudo.toFormattedString(indent + CONNECTOR, true));  // Indenta corretamente o conteúdo
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);  // Consistente com outros nós, começa do nível 0
    }
}
