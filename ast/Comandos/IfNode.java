package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;
import java.util.List;

public class IfNode extends ASTNode {
    public final ASTNode condicao;
    public final BlockNode thenBlock;
    public final List<ElseIfNode> elseIfs;
    public final BlockNode elseBlock;

    public IfNode(ASTNode condicao, BlockNode thenBlock, 
                 List<ElseIfNode> elseIfs, BlockNode elseBlock) {
        this.condicao = condicao;
        this.thenBlock = thenBlock;
        this.elseIfs = elseIfs;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(indent)).append("If\n")
          .append(condicao.toString(indent + 1))
          .append(thenBlock.toString(indent + 1));
        
        for (ElseIfNode elif : elseIfs) {
            sb.append(elif.toString(indent + 1));
        }
        
        if (elseBlock != null) {
            sb.append("  ".repeat(indent)).append("Else\n")
              .append(elseBlock.toString(indent + 1));
        }
        return sb.toString();
    }
}