package Compilador.ast.Expressoes;

import Compilador.ast.ASTNode;
import java.util.List;

public class CallExpr extends ASTNode {
    public final String functionName;
    public final List<ASTNode> arguments;

    public CallExpr(String functionName, List<ASTNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(indent))
          .append("Call(").append(functionName).append(")\n");
        
        for (ASTNode arg : arguments) {
            sb.append(arg.toString(indent + 1));
        }
        return sb.toString();
    }
}