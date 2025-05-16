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
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(indent)).append("Switch\n")
          .append("  ".repeat(indent+1)).append("Expressao:\n")
          .append(expressao.toString(indent+2));
        
        for (CaseNode caso : casos) {
            sb.append(caso.toString(indent+1));
        }
        
        if (padrao != null) {
            sb.append("  ".repeat(indent+1)).append("Padrao:\n")
              .append(padrao.toString(indent+2));
        }
        return sb.toString();
    }
}