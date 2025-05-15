package lexico.tokens;


import java.text.CharacterIterator;

import lexico.Token;

public class MathOperator extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '+':
                code.next();
                if (code.current() == '+') {
                    code.next();
                    return new Token("INCREMENT", "++");
                }
                return new Token("PLUS", "+");

            case '-':
                code.next();
                if (code.current() == '>') {
                    code.next();
                    return new Token("ASSIGN", "->");
                }
                if (code.current() == '-') {
                    code.next();
                    return new Token("DECREMENT", "--");
                }
                return new Token("MINUS", "-");

            case '*':
                code.next();
                return new Token("MULTIPLY", "*");

            case '/':
                code.next();
                return new Token("DIVIDE", "/");

            case '^':
                code.next();
                return new Token("EXPONENT", "^");

            default:
                return null;
        }
    }
}

