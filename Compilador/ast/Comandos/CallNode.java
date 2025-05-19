package Compilador.ast.Comandos;

import Compilador.ast.*;
import java.util.List;

public class CallNode extends ASTNode {
    private final String funcName;
    private final List<ASTNode> args;

    public CallNode(String funcName, List<ASTNode> args) {
        this.funcName = funcName.startsWith(":") ? funcName.substring(1) : funcName;
        this.args = args;
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(isLast ? "└── " : "├── ");
        sb.append("Call: ").append(funcName).append("\n");

        String childIndent = indent + (isLast ? "    " : "│   ");

        if (!args.isEmpty()) {
            sb.append(childIndent).append("├── Arguments:\n");
            for (int i = 0; i < args.size(); i++) {
                boolean lastArg = (i == args.size() - 1);
                sb.append(childIndent)
                  .append(lastArg ? "└── " : "├── ")
                  .append(args.get(i).toFormattedString(childIndent + (lastArg ? "    " : "│   "), lastArg));
                if (!lastArg) {
                    sb.append("\n");
                }
            }
        } else {
            // Nenhum argumento informado
            sb.append(childIndent).append("└── Arguments: None\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }

    public String getFunctionName() {
        return funcName;
    }

    public List<ASTNode> getArguments() {
        return args;
    }
}
