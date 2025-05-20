package Compilador.ast.Comandos;

import Compilador.ast.ASTNode;

public class CommentNode extends ASTNode {
    public final String texto;
    private static final int MAX_DISPLAY_LENGTH = 50;

    public CommentNode(String texto) {
        // Normaliza espaços e quebras de linha
        this.texto = texto.replaceAll("\\s+", " ").trim();
    }

    @Override
    public String toFormattedString(String indent, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        
        // Prefixo visual se não for raiz
        sb.append(indent).append(isLast ? "└── " : "├── ");
        
        // Conteúdo truncado se necessário
        if (texto.length() > MAX_DISPLAY_LENGTH) {
            sb.append(texto, 0, MAX_DISPLAY_LENGTH - 3).append("..."); 
        } else {
            sb.append(texto);
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormattedString("", true); 
    }

    public String getFullText() {
        return texto;
    }
}
