package Compilador.ast.Comandos;

import Compilador.ast.*;
import java.util.List;

public class IfNode extends ASTNode {
    public final ASTNode condicao;
    public final BlockNode thenBlock;
    public final List<ElseIfNode> elseIfs;
    public final BlockNode elseBlock;

    // Construtor da classe IfNode
    public IfNode(ASTNode condicao, BlockNode thenBlock,
                 List<ElseIfNode> elseIfs, BlockNode elseBlock) {
        this.condicao = condicao;
        this.thenBlock = thenBlock;
        this.elseIfs = elseIfs;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Cabeçalho do If
        sb.append(indent).append(isLast ? "└── " : "├── ").append("If\n");
        
        String childIndent = indent + (isLast ? "    " : "│   ");
        
        // Condição
        sb.append(childIndent).append("├── Condition:\n")
          .append(condicao.toFormattedString(childIndent + "│   ", false));
        
        // Then
        sb.append(childIndent).append("├── Then:\n")
          .append(thenBlock.toFormattedString(childIndent + "│   ", elseIfs.isEmpty() && elseBlock == null));
        
        // ElseIfs
        for (int i = 0; i < elseIfs.size(); i++) {
            boolean isLastElseIf = (i == elseIfs.size() - 1) && elseBlock == null;
            sb.append(childIndent)
              .append(isLastElseIf ? "└── " : "├── ")
              .append("ElseIf ").append(i + 1).append(":\n")
              .append(elseIfs.get(i).toFormattedString(childIndent + (isLastElseIf ? "    " : "│   "), isLastElseIf));
        }
        
        // Else block
        if (elseBlock != null) {
            sb.append(childIndent).append("└── Else:\n")
              .append(elseBlock.toFormattedString(childIndent + "    ", true));
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true); // Usar a versão formatada para consistência
    }
}
