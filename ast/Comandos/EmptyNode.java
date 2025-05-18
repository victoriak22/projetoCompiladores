package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class EmptyNode extends ASTNode {
    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Add indentation for the node level
        sb.append(indent);
        
        // Determine the connector based on whether it's the last node or not
        if (isLast) {
            sb.append("└── ");
        } else {
            sb.append("├── ");
        }
        
        // Add the "Empty" node representation
        sb.append("Empty Node");  // Clearer naming to indicate an empty node

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);  // Consistent with other nodes
    }
}
