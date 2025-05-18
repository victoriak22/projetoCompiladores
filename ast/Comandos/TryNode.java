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
        
        // Try header
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Try\n");
        
        // Try block (using LAST since it's the only child)
        sb.append(indent).append("    ").append("└── ").append("Block:\n")
          .append(tryBlock.toFormattedString(indent + "    ", true));  // Pass indentation and true since it's the last node
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);  // Start with no indent and treat it as the last node
    }
}
