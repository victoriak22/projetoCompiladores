package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.*;

public class PrintToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == '=') {
            int startPos = code.getIndex();
            code.next(); // Consome '='

            if (code.current() == '"') {
                code.next(); // Consome '"'
                StringBuilder content = new StringBuilder("=\"");

                // Coleta o conteúdo até a próxima aspas
                while (code.current() != '"' && code.current() != CharacterIterator.DONE) {
                    content.append(code.current());
                    code.next();
                }

                if (code.current() == '"') {
                    content.append('"');
                    code.next(); // Consome a aspas final
                    return new Token("PRINT", content.toString());
                }
            } else if (code.current() == '\'') {
                // Caso para sintaxe alternativa: ='texto'
                code.next(); // Consome a aspa simples inicial
                StringBuilder content = new StringBuilder("='");

                // Coleta o conteúdo até a próxima aspa simples
                while (code.current() != '\'' && code.current() != CharacterIterator.DONE) {
                    content.append(code.current());
                    code.next();
                }

                if (code.current() == '\'') {
                    content.append('\'');
                    code.next(); // Consome a aspa simples final
                    return new Token("PRINT", content.toString());
                }
            }

            // Se não fechou corretamente, volta ao início
            code.setIndex(startPos);
        }
        return null;
    }
}