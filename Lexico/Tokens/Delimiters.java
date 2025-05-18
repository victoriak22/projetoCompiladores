package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;

import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class Delimiters extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        char c = code.current();
        if (c == ',' || c == ';' || c == '(' || c == ')' || c == '{' || c == '}') {
            String lexema = String.valueOf(c);
            code.next();
            return new Token("DELIMITER", lexema);
        }
        return null;
    }
}
