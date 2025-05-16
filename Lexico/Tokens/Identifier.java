package Compilador;

import java.text.CharacterIterator;

public class Identifier extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() != ':') {
            return null; // Identificadores devem começar com :
        }

        int startPos = code.getIndex();
        StringBuilder sb = new StringBuilder();
        sb.append(code.current()); // Adiciona o ':'
        code.next(); // Consome o ':'

        // Verifica se tem pelo menos um caractere válido
        if (!Character.isLetter(code.current()) && code.current() != '_') {
            code.setIndex(startPos); // Rollback
            return null;
        }

        // Constrói o restante do identificador
        while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
            sb.append(code.current());
            code.next();
        }

        // Verifica se não é apenas ":" 
        if (sb.length() == 1) {
            code.setIndex(startPos);
            return null;
        }

        return new Token("ID", sb.toString());
    }
}