import java.text.CharacterIterator;

public class RelationalOperator extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '=':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("EQUALS", "==");
                }
                return new Token("ASSIGN", "=");
            case '>':
                code.next();
                return new Token("GREATER", ">");
            case '<':
                code.next();
                return new Token("LESS", "<");
            case '!':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("NOT_EQUAL", "!=");
                }
                return null;
            case CharacterIterator.DONE:
                code.next();
                return new Token("EOF", "EOF");
            default:
                return null;
        }
    }
}
