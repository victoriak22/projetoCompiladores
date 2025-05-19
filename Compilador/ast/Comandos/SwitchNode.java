package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;
import java.util.List;

public class SwitchNode extends ASTNode {
    public final ASTNode expressao;
    public final List<CaseNode> casos;
    public final ASTNode padrao;

    public SwitchNode(ASTNode expressao, List<CaseNode> casos, ASTNode padrao) {
        this.expressao = expressao;
        this.casos = casos;
        this.padrao = padrao;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Switch header
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Switch\n");
        
        // Expression
        sb.append(indent).append("│   ").append("└── ").append("Expressao:\n")
          .append(expressao.toFormattedString(indent + "    ", false));  // Indentation for child, not last
        
        // Cases
        for (int i = 0; i < casos.size(); i++) {
            boolean isLastCase = (i == casos.size() - 1) && (padrao == null);
            sb.append(indent)
              .append(isLastCase ? "└── " : "├── ")
              .append("Case ").append(i + 1).append(":\n")
              .append(casos.get(i).toFormattedString(indent + "    ", isLastCase));
        }
        
        // Default case
        if (padrao != null) {
            sb.append(indent).append("└── ").append("Default:\n")
              .append(padrao.toFormattedString(indent + "    ", true));
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);  // Use the root level (no indent) and mark it as the last node
    }
}
