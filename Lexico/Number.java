package Compilador.Lexico;

import java.text.CharacterIterator;


public class Number extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (Character.isDigit(code.current())) {
            String number = readNumber(code);
            if (code.current() == '.') {
                code.next();
                if (Character.isDigit(code.current())) {
                    number += "." + readNumber(code);
                    return new Token("FLOAT", number);
                } else {
                    code.previous();
                }
            }
            return new Token("INTEGER", number);
        }
        return null;
    }

    private String readNumber(CharacterIterator code) {
        StringBuilder number = new StringBuilder();
        while (Character.isDigit(code.current())) {
            number.append(code.current());
            code.next();
        }
        return number.toString();
    }
}
