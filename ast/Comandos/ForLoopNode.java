package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class ForLoopNode extends ASTNode {
    public final ASTNode inicializacao;
    public final ASTNode condicao;
    public final ASTNode incremento;
    public final BlockNode corpo;

    public ForLoopNode(ASTNode inicializacao, ASTNode condicao, 
                      ASTNode incremento, BlockNode corpo) {
        this.inicializacao = inicializacao;
        this.condicao = condicao;
        this.incremento = incremento;
        this.corpo = corpo;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "ForLoop\n" +
               inicializacao.toString(indent + 1) +
               condicao.toString(indent + 1) +
               incremento.toString(indent + 1) +
               corpo.toString(indent + 1);
    }
}