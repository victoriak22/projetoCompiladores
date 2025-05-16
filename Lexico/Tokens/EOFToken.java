package Compilador;

import java.text.CharacterIterator;

public class EOFToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == CharacterIterator.DONE) {
            return new Token("EOF", "EOF");
        }
        return null;
    }
}