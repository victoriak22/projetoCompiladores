package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class TryNode extends ASTNode {
    public final BlockNode tryBlock;

    public TryNode(BlockNode tryBlock) {
        this.tryBlock = tryBlock;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Try\n");
        
        sb.append(indent).append("    ").append("└── ").append("Block:\n")
          .append(tryBlock.toFormattedString(indent + "    ", true));
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true); 
    }
}
