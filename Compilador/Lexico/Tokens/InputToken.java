package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class InputToken extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == '$') {
            StringBuilder sb = new StringBuilder();
            sb.append(code.current()); // Adiciona o símbolo $
            code.next(); // Consome o $
            
            // Verifica se há o padrão "ler("
            if (code.current() == 'l') {
                sb.append(code.current()); // l
                code.next();
                if (code.current() == 'e') {
                    sb.append(code.current()); // e
                    code.next();
                    if (code.current() == 'r') {
                        sb.append(code.current()); // r
                        code.next();
                        if (code.current() == '(') {
                            sb.append(code.current()); // (
                            code.next();
                            
                            // Agora devemos ter um ID (variável)
                            while (code.current() != ')' && code.current() != CharacterIterator.DONE) {
                                sb.append(code.current());
                                code.next();
                            }
                            
                            if (code.current() == ')') {
                                sb.append(code.current()); // )
                                code.next();
                                return new Token("INPUT", sb.toString());
                            }
                        }
                    }
                }
            }
            
            // Se falhar, retorna nulo (não reconheceu o padrão)
            return null;
        }
        return null;
    }
}