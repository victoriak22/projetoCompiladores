package lexico.tokens;


import java.text.CharacterIterator;

import lexico.Token;

public class PrintToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() != '=') {
            return null;
        }
        code.next(); // Consome o '='
        
        // Verifica aspas duplas de abertura
        if (code.current() != '"') {
            throw new RuntimeException("Comando print deve começar com =\"");
        }
        code.next();
        
        StringBuilder content = new StringBuilder();
        boolean inString = false;
        
        while (code.current() != '"' && code.current() != CharacterIterator.DONE) {
            // Trata aspas simples internas
            if (code.current() == '\'') {
                inString = !inString;
                code.next();
                continue;
            }
            
            // Adiciona conteúdo (incluindo identificadores como :var)
            content.append(code.current());
            code.next();
        }
        
        // Verifica aspas duplas de fechamento
        if (code.current() != '"') {
            throw new RuntimeException("Aspa dupla não fechada no print");
        }
        code.next();
        
        return new Token("PRINT", content.toString().trim());
    }
}