package Compilador;

import java.text.CharacterIterator;

public class StringIdentifier extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() != '"') {
            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append(code.current()); // adiciona a primeira aspa
        code.next();

        while (code.current() != CharacterIterator.DONE && code.current() != '"') {
            value.append(code.current());
            code.next();
        }

        if (code.current() == '"') {
            value.append(code.current()); // fecha aspas
            code.next();
            return new Token("STRING", value.toString());
        }

        // Se não encontrar aspas de fechamento
        return null;
    }
}
