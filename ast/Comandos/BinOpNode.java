package Compilador.ast.Comandos;  // Mudei para Expressoes (mais semântico)

import Compilador.ast.ASTNode;

public class BinOpNode extends ASTNode {
    // Mudei para public final (imutável, melhor para AST)
    public final ASTNode left;
    public final String op;
    public final ASTNode right;

    public BinOpNode(ASTNode left, String op, ASTNode right) {
        if (left == null || right == null || op == null) {
            throw new IllegalArgumentException("Operandos/operador não podem ser nulos");
        }
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "BinOp(" + op + ")\n" +
               left.toString(indent + 1) + 
               right.toString(indent + 1);
    }

    // Método útil para análise semântica posterior
    public boolean isArithmetic() {
        return List.of("+", "-", "*", "/").contains(op);
    }
}