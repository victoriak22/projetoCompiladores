package lexico.tokens;


import java.text.CharacterIterator;

import lexico.Token;

public class Delimiters extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '\n':
                code.next();
                return new Token("NEWLINE", "\\n");
            case '\r':
                code.next();
                return new Token("CARRIAGE_RETURN", "\\r");
            case '\t':
                code.next();
                return new Token("TAB", "\\t");
            case CharacterIterator.DONE:
                code.next();
                return new Token("EOF", "EOF");
            default:
                return null;
        }
    }
}
