package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class StringIdentifier extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        // Armazena a posição inicial para poder voltar em caso de erro
        int startPos = code.getIndex();
        
        // Verifica se começa com aspas simples ou duplas
        char quoteType;
        if (code.current() == '\'') {
            quoteType = '\'';
        } else if (code.current() == '"') {
            quoteType = '"';
        } else {
            return null; // Não é uma string
        }
        
        StringBuilder value = new StringBuilder();
        value.append(code.current()); // Adiciona a aspa de abertura
        code.next();
        
        // Lê todos os caracteres até encontrar a aspa de fechamento correspondente
        while (code.current() != CharacterIterator.DONE && code.current() != quoteType) {
            // Tratamento de escape para permitir aspas dentro da string
            if (code.current() == '\\') {
                value.append(code.current()); // Adiciona a barra invertida
                code.next();
                
                if (code.current() == CharacterIterator.DONE) {
                    // Se atingir o fim antes de completar o escape, retorna null
                    code.setIndex(startPos);
                    return null;
                }
                
                // Adiciona o caractere de escape (pode ser a própria aspa)
                value.append(code.current());
                code.next();
            } else {
                value.append(code.current());
                code.next();
            }
        }
        
        // Verifica se a string foi fechada corretamente
        if (code.current() == quoteType) {
            value.append(code.current()); // Adiciona a aspa de fechamento
            code.next(); // Avança para o próximo token
            return new Token("STRING", value.toString());
        } else {
            // Se chegou ao fim sem encontrar a aspa de fechamento, retorna null
            code.setIndex(startPos);
            return null;
        }
    }
}