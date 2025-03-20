import java.text.CharacterIterator;

public class Limiters extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '(':
                code.next();
                return new Token("LEFT_PAREN", "(");
            case ')':
                code.next();
                return new Token("RIGHT_PAREN", ")");
            case '{':
                code.next();
                return new Token("LEFT_BRACE", "{");
            case '}':
                code.next();
                return new Token("RIGHT_BRACE", "}");
            case '[':
                code.next();
                return new Token("LEFT_BRACKET", "[");
            case ']':
                code.next();
                return new Token("RIGHT_BRACKET", "]");
            case ';':
                code.next();
                return new Token("SEMICOLON", ";");
            case ':':
                code.next();
                return new Token("COLON", ":");
            case ',':
                code.next();
                return new Token("COMMA", ",");
            case CharacterIterator.DONE:
                code.next();
                return new Token("EOF", "EOF");
            default:
                return null;
        }
    }
}