import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Token> tokens = null;
        String data = "\tdeus verificarNumero(inteiro x) { se (x > 10) { amen \"Maior que 10!\"; }\n senao { amen 'Menor ou igual a 10!'; }\r }";
        Lexer lexer = new Lexer(data);
        tokens = lexer.getTokens();
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}