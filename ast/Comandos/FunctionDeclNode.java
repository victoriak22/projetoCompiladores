package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;
import java.util.List;

public class FunctionDeclNode extends ASTNode {
    public final String nome;
    public final List<ASTNode> parametros; // Lista de ParamNode
    public final ASTNode corpo;

    public FunctionDeclNode(String nome, List<ASTNode> parametros, ASTNode corpo) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = corpo;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(indent)).append("Func ").append(nome).append("\n");
        for (ASTNode p : parametros) {
            sb.append(p.toString(indent + 1));
        }
        sb.append(corpo.toString(indent + 1));
        return sb.toString();
    }
}