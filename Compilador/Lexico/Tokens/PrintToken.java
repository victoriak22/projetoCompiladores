package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.*;

public class PrintToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == 'p') {
            int startPos = code.getIndex();
            code.next(); // Consome 'p'

            if (code.current() == '(') {
                code.next(); // Consome '('
                StringBuilder content = new StringBuilder("p(");

                boolean expectMore = true;

                while (expectMore && code.current() != CharacterIterator.DONE) {
                    // Ignora espaços
                    while (Character.isWhitespace(code.current())) {
                        code.next();
                    }

                    // Verifica se é uma string entre aspas
                    if (code.current() == '"') {
                        content.append('"');
                        code.next();
                        while (code.current() != '"' && code.current() != CharacterIterator.DONE) {
                            content.append(code.current());
                            code.next();
                        }
                        if (code.current() == '"') {
                            content.append('"');
                            code.next();
                        }
                    }
                    // Verifica se é uma variável ou símbolo
                    else if (Character.isLetterOrDigit(code.current()) || code.current() == ':' || code.current() == '+'
                            || code.current() == '-') {
                        while (code.current() != ',' && code.current() != ')'
                                && code.current() != CharacterIterator.DONE) {
                            content.append(code.current());
                            code.next();
                        }
                    }

                    // Verifica próximo caractere
                    if (code.current() == ',') {
                        content.append(", ");
                        code.next(); // Consome vírgula e continua
                    } else if (code.current() == ')') {
                        content.append(')');
                        code.next(); // Consome ')'
                        expectMore = false;
                    } else {
                        break; // Caso de erro ou fim inesperado
                    }
                }

                return new Token("PRINT", content.toString());
            }

            // Falha, volta ao início
            code.setIndex(startPos);
        }
        return null;
    }
}
