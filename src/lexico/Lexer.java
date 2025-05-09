
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.ArrayList;

public class Lexer {
    private List<Token> tokens;
    private List<AFD> afds;
    private CharacterIterator code;

    public Lexer(String code) {
        tokens = new ArrayList<>();
        afds = new ArrayList<>();
        this.code = new StringCharacterIterator(code);
        afds.add(new ReservedWords());
        afds.add(new MathOperator());
        afds.add(new Number());
        afds.add(new RelationalOperator());
        afds.add(new Identifier());
        afds.add(new Limiters());
        afds.add(new Delimiters());
        afds.add(new StringIdentifier());
    }

    public void skipWhiteSpace() {
        while (code.current() == ' ' || code.current() == '\n') {
            code.next();
        }
    }

    public List<Token> getTokens() {
        Token t;
        do {
            skipWhiteSpace();
            t = searchNextToken();
            if (t == null) {
                error();
            }
            tokens.add(t);
        } while (t.getTipo() != "EOF");
        return tokens;

    }

    private Token searchNextToken() {
        int pos = code.getIndex();
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);
            if (t != null) {
                return t;
            }
            code.setIndex(pos);
        }
        return null;
    }

    private void error() {
        throw new RuntimeException("Error: token not recognized: " + code.current());

    }
}
