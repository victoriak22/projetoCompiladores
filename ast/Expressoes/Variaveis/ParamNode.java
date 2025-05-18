package Compilador.ast.Expressoes.Variaveis;

import Compilador.ast.ASTNode;

public class ParamNode extends ASTNode {
    private final String name;

    public ParamNode(String name) {
        this.name = name.startsWith(":") ? name.substring(1).trim() : name.trim();
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        // Usando os conectores novos sem as constantes da superclasse
        return indent + (isLast ? "└── " : "├── ") + "Param: " + name + "\n";
    }

    @Override
    public String toString() {
        return toFormattedString("", true);
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return ":" + name;
    }
}
