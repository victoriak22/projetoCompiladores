package Compilador.ast.Comandos;

import Compilador.ast.*;

public class BinOpNode extends ASTNode {
    private final ASTNode left;
    private final String op;
    private final ASTNode right;

    public BinOpNode(ASTNode left, String op, ASTNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(isLast ? "└── " : "├── ").append("BinOp: ").append(op).append("\n");

        String childIndent = indent + (isLast ? "    " : "│   ");

        sb.append(childIndent).append("├── Left:\n");
        sb.append(left.toFormattedString(childIndent + "│   ", false)).append("\n");

        sb.append(childIndent).append("└── Right:\n");
        sb.append(right.toFormattedString(childIndent + "    ", true));

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }

    public ASTNode getLeft() { return left; }
    public String getOperator() { return op; }
    public ASTNode getRight() { return right; }
}
