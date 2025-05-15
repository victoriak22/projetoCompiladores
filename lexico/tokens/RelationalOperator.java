package lexico.tokens;


import java.text.CharacterIterator;

import lexico.Token;

public class RelationalOperator extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '-':
                code.next();
                if (code.current() == '>') {
                    code.next();
                    return new Token("ASSIGN", "->");
                }
                return null;

            case '=':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("EQUALS", "==");
                }
                throw new RuntimeException("Símbolo '=' inválido. Use '==' para comparação ou '->' para atribuição");

            case '>':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("GREATER_EQUAL", ">=");
                }
                return new Token("GREATER", ">");

            case '<':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("LESS_EQUAL", "<=");
                }
                return new Token("LESS", "<");

            case '!':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("NOT_EQUAL", "!=");
                }
                return null;

            default:
                return null;
        }
    }
}