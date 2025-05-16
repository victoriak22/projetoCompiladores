package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class PrintNode extends ASTNode {
    public final ASTNode expression;
    
    public PrintNode(ASTNode expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Print\n" + 
               expression.toString(indent + 1);
    }
}