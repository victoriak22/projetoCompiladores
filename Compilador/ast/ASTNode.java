package Compilador.ast;

public abstract class ASTNode {    
    public abstract String toFormattedString(String indent, boolean isLast);

    public String toFormattedString() {
        return toFormattedString("", true);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
