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
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        // Cabeçalho da função matemática
        sb.append(indent).append(isLast ? "└── " : "├── ")
          .append("MathFunc: ").append(funcao).append("\n");

        String childIndent = indent + (isLast ? "    " : "│   ");

        if (!argumentos.isEmpty()) {
            sb.append(childIndent).append("├── Arguments:\n");

            for (int i = 0; i < argumentos.size(); i++) {
                boolean lastArg = (i == argumentos.size() - 1);
                sb.append(argumentos.get(i).toFormattedString(childIndent + (lastArg ? "    " : "│   "), lastArg));
                if (!lastArg) sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }

    public String getFuncao() {
        return funcao;
    }

    public List<ASTNode> getArgumentos() {
        return argumentos;
    }
}
