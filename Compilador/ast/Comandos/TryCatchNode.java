package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class TryCatchNode extends ASTNode {
    public final BlockNode tryBlock;
    public final String exceptionVar;
    public final BlockNode catchBlock;

    public TryCatchNode(BlockNode tryBlock, String exceptionVar, BlockNode catchBlock) {
        this.tryBlock = tryBlock;
        this.exceptionVar = exceptionVar;
        this.catchBlock = catchBlock;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Try-Catch header
        sb.append(indent).append(isLast ? "└── " : "├── ").append("TryCatch\n");
        
        // Try block
        sb.append(indent).append("│   ").append("└── ").append("Try:\n")
          .append(tryBlock.toFormattedString(indent + "    ", false));  // Passa indentação e 'false' porque não é o último nó
        
        // Catch block
        sb.append(indent).append("└── ").append("Catch(").append(exceptionVar).append("):\n")
          .append(catchBlock.toFormattedString(indent + "    ", true));  // Passa indentação e 'true' para o último nó
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);  // No indentação inicial e trata como o último nó
    }
}
