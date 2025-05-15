package Compilador;

import java.io.IOException;
import java.util.List;

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
        parser.main(); // Inicia análise sintática
    }
}

