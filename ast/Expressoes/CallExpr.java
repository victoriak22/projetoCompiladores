package Compilador.ast.Expressoes;

import java.util.List;
import Compilador.ast.*;

public class CallExpr extends ASTNode {
    private final String functionName;
    private final List<ASTNode> args;

    public CallExpr(String functionName, List<ASTNode> args) {
        this.functionName = functionName.startsWith(":") ? 
                            functionName.substring(1) : functionName;
        this.args = args;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        // Cabeçalho da chamada de função
        sb.append(indent).append(isLast ? "└── " : "├── ")
          .append("Call: ").append(functionName).append("\n");

        String childIndent = indent + (isLast ? "    " : "│   ");

        // Se houver argumentos
        if (!args.isEmpty()) {
            sb.append(childIndent).append("├── Arguments:\n");

            for (int i = 0; i < args.size(); i++) {
                boolean lastArg = (i == args.size() - 1);
                sb.append(args.get(i).toFormattedString(childIndent + (lastArg ? "    " : "│   "), lastArg));
                if (!lastArg) sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }
}
