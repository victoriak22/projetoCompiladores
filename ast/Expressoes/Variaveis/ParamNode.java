package Compilador.ast.Expressoes.Variaveis;

import Compilador.ast.ASTNode;

public class ParamNode extends ASTNode {
    private final String nomeParametro;

    public ParamNode(String nomeParametro) {
        this.nomeParametro = nomeParametro;
    }

    @Override
    public String toString(int indent) {
        return "  ".repeat(indent) + "Param(" + nomeParametro + ")\n";
    }

    // Getter (opcional, para uso futuro)
    public String getNome() {
        return nomeParametro;
    }
}