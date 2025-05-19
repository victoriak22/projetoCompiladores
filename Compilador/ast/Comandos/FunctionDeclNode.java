package Compilador.ast.Comandos;

import Compilador.ast.*;
import java.util.List;
import Compilador.ast.Expressoes.Variaveis.*;

public class FunctionDeclNode extends ASTNode {
    private final String name;
    private final List<ParamNode> params;
    private final BlockNode body;

    public FunctionDeclNode(String name, List<ParamNode> params, BlockNode body) {
        this.name = name.startsWith(":") ? name.substring(1) : name;
        this.params = params;
        this.body = body;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(isLast ? "└── " : "├── ").append("Function: ").append(name).append("\n");
        String childIndent = indent + (isLast ? "    " : "│   ");

        if (!params.isEmpty()) {
            sb.append(childIndent).append("├── Parameters:\n");
            String paramIndent = childIndent + "│   ";
            for (int i = 0; i < params.size(); i++) {
                boolean lastParam = (i == params.size() - 1);
                sb.append(params.get(i).toFormattedString(paramIndent, lastParam));
                if (!lastParam) sb.append("\n");
            }
        } else {
            sb.append(childIndent).append("├── Parameters: None\n");
        }

        sb.append(childIndent).append("└── Body:\n");
        sb.append(body.toFormattedString(childIndent + "    ", true));

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}
