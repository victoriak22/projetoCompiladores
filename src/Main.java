import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Token> tokens = null;
        String data = "if x == 1 then x + 1 else x - 1.0";
        Lexer lexer = new Lexer(data);
        tokens = lexer.getTokens();
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}