package lexico.tokens;


import java.text.CharacterIterator;

import lexico.Token;

public class EOFToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == CharacterIterator.DONE) {
            return new Token("EOF", "EOF");
        }
        return null;
    }
}