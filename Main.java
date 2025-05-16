package Compilador;

import java.io.IOException;
import java.util.List;

import Compilador.ast.ASTNode;

public class Main {
    public static void main(String[] args) throws IOException {
        String data = 
        "deus :multiplicar -> :A, :B {\n" +
        "  :resultado -> :A * :B\n" +
        "  amen :resultado\n" +
        "}\n" +
        "\n" +
        ":resultadoMultiplicacao -> :multiplicar(3, 4)\n" +
        "=\"'Resultado da multiplicação: ' :resultadoMultiplicacao\"\n";

        Lexer lexer = new Lexer(data);
        List<Token> tokens = lexer.getTokens();

        // 🟡 Imprime todos os tokens gerados
        System.out.println("Tokens gerados:");
        for (Token t : tokens) {
            System.out.println(t);
        }
        System.out.println("\nLéxico finalizado...");

        System.out.println("Inicializando parser...");

        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();  // Agora retorna a AST

        if (ast != null) {
            System.out.println("\n✅ Programa válido! AST construída.");
            // Opcional: Salvar a AST ou passar para o interpretador
        } else {
            System.err.println("\n❌ Erros encontrados durante a análise");
        }
    }
}

