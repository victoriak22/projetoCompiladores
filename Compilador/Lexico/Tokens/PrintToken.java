package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.*;

public class PrintToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == 'p') {
            int startPos = code.getIndex();
            code.next(); // Consome 'p'
            
            // Verifica se o caractere seguinte é um parêntese de abertura
            if (code.current() == '(') {
                code.next(); // Consome '('
                StringBuilder content = new StringBuilder("p(");
                
                // Verifica se é uma string com aspas
                if (code.current() == '"') {
                    content.append('"');
                    code.next(); // Consome '"'
                    
                    // Coleta o conteúdo até a próxima aspas
                    while (code.current() != '"' && code.current() != CharacterIterator.DONE) {
                        content.append(code.current());
                        code.next();
                    }
                    
                    if (code.current() == '"') {
                        content.append('"');
                        code.next(); // Consome a aspas final
                        
                        // Verifica se há fechamento do parêntese
                        if (code.current() == ')') {
                            content.append(')');
                            code.next(); // Consome ')'
                            return new Token("PRINT", content.toString());
                        }
                    }
                } else {
                    // Caso seja uma expressão ou variável sem aspas
                    while (code.current() != ')' && code.current() != CharacterIterator.DONE) {
                        content.append(code.current());
                        code.next();
                    }
                    
                    if (code.current() == ')') {
                        content.append(')');
                        code.next(); // Consome ')'
                        return new Token("PRINT", content.toString());
                    }
                }
            }
            
            // Se não seguir o padrão esperado, volta ao início
            code.setIndex(startPos);
        }
        return null;
    }
}