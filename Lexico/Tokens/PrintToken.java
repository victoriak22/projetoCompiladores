package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.*;

public class PrintToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        // Verifica o padrão =" para identificar PRINT
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
            }
            // Se não fechou corretamente, volta ao início
            code.setIndex(startPos);
        }
        return null;
    }
}