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
    public String toString(int indent) {
        return "  ".repeat(indent) + "TryCatch\n" +
               "  ".repeat(indent+1) + "Try:\n" + tryBlock.toString(indent+2) +
               "  ".repeat(indent+1) + "Catch(" + exceptionVar + "):\n" + 
               catchBlock.toString(indent+2);
    }
}