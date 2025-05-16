package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class CaseNode extends ASTNode {
    public final ASTNode valor;
    public final ASTNode corpo;

    public CaseNode(ASTNode valor, ASTNode corpo) {
        this.valor = valor;
        this.corpo = corpo;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Case\n" +
               "  ".repeat(indent+1) + "Valor:\n" + valor.toString(indent+2) +
               "  ".repeat(indent+1) + "Corpo:\n" + corpo.toString(indent+2);
    }
}