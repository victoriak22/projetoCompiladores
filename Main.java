import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String data = "deus multiplicar -> A, B {\r\n" + //
                        "\t:resultado -> A * B\r\n" + //
                        "\tamen :resultado\r\n" + //
                        "}\r\n" + //
                        "\r\n" + //
                        "resultadoMultiplicacao -> multiplicar(3, 4)\r\n" + //
                        "=\"Resultado da multiplicação: \" + resultadoMultiplicacao";
        Lexer lexer = new Lexer(data);
        List<Token> tokens = lexer.getTokens();
        tokens.add(new Token("EOF", "EOF")); // Adicione EOF ao final

        // System.out.println(tokens);

        System.out.println("Léxico finalizado...");
        System.out.println("Inicializando parser...");

        Parser parser = new Parser(tokens);
        parser.main(); // inicia análise sintática
    }
}
