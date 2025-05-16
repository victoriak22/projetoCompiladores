package Compilador.ast.Expressoes;

import Compilador.ast.ASTNode;
import java.util.List;

public class MathFuncNode extends ASTNode {
    public final String funcao;
    public final List<ASTNode> argumentos;

    public MathFuncNode(String funcao, List<ASTNode> argumentos) {
        this.funcao = funcao;
        this.argumentos = argumentos;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(indent))
          .append("MathFunc:").append(funcao).append("\n");
        
        for (ASTNode arg : argumentos) {
            sb.append(arg.toString(indent + 1));
        }
        return sb.toString();
    }
}