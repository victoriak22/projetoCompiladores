// AssignNode.java
package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;
import Compilador.ast.Expressoes.Variaveis.VarNode;

public class AssignNode extends ASTNode {
    public final VarNode variable;
    public final ASTNode expression;

    public AssignNode(VarNode variable, ASTNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Assign\n" +
               variable.toString(indent + 1) +
               expression.toString(indent + 1);
    }
}