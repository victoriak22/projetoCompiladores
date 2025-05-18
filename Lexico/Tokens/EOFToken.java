package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;

import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class EOFToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == CharacterIterator.DONE) {
            return new Token("EOF", "EOF");
        }
        return null;
    }
}