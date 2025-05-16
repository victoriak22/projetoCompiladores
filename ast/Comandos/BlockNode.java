package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;
import java.util.List;

public class BlockNode extends ASTNode {
    public final List<ASTNode> commands;
    
    public BlockNode(List<ASTNode> commands) {
        this.commands = commands;
    }
    
    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(indent)).append("Block\n");
        for (ASTNode cmd : commands) {
            sb.append(cmd.toString(indent + 1));
        }
        return sb.toString();
    }
}