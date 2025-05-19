package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class InputToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        StringBuilder sb = new StringBuilder();
        int startPos = code.getIndex();
        
        // Verificando se é o comando de input
        if (code.current() == '$') {
            sb.append(code.current()); // Adiciona o $
            code.next();
            
            // Verifica se o próximo token é "ler"
            if (code.current() == 'l') {
                sb.append(code.current());
                code.next();
                if (code.current() == 'e') {
                    sb.append(code.current());
                    code.next();
                    if (code.current() == 'r') {
                        sb.append(code.current());
                        code.next();
                        // Verificar se tem parêntese
                        if (code.current() == '(') {
                            sb.append(code.current());
                            code.next();
                            
                            // Captura o identificador da variável
                            // Geralmente uma variável PSALMS começa com :
                            if (code.current() == ':') {
                                while (code.current() != ')' && code.current() != CharacterIterator.DONE) {
                                    sb.append(code.current());
                                    code.next();
                                }
                                
                                if (code.current() == ')') {
                                    sb.append(code.current());
                                    code.next();
                                    return new Token("INPUT", sb.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Se chegou aqui, não é um comando de input válido
        code.setIndex(startPos);
        return null;
    }
}