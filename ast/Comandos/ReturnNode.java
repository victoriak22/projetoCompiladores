// ReturnNode.java
package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class ReturnNode extends ASTNode {
    public final ASTNode expressao;

    public ReturnNode(ASTNode expressao) {
        this.expressao = expressao;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Return\n" + 
               expressao.toString(indent + 1);
    }
}