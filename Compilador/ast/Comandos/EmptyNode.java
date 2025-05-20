package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class EmptyNode extends ASTNode {
    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(indent);
        
        if (isLast) {
            sb.append("└── ");
        } else {
            sb.append("├── ");
        }
        
        sb.append("Empty Node");  

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}
