package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class TryNode extends ASTNode {
    public final BlockNode tryBlock;

    public TryNode(BlockNode tryBlock) {
        this.tryBlock = tryBlock;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Try\n" +
               tryBlock.toString(indent+1);
    }
}