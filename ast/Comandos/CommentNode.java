package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class CommentNode extends ASTNode {
    public final String texto;

    public CommentNode(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Comment: " + texto + "\n";
    }
}