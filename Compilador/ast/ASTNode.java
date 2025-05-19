package Compilador.ast;

public abstract class ASTNode {
    // Removido CONNECTOR e LAST pois não são mais usados
    
    public abstract String toFormattedString(String indent, boolean isLast);

    public String toFormattedString() {
        return toFormattedString("", true);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
