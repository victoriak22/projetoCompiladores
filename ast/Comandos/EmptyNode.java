package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class EmptyNode extends ASTNode {
    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Empty\n";
    }
}