package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class WhileLoopNode extends ASTNode {
    public final ASTNode condicao;
    public final BlockNode corpo;

    public WhileLoopNode(ASTNode condicao, BlockNode corpo) {
        this.condicao = condicao;
        this.corpo = corpo;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "WhileLoop\n" +
               condicao.toString(indent + 1) +
               corpo.toString(indent + 1);
    }
}