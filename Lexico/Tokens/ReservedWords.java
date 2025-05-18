package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;

import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class ReservedWords extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        // Primeiro verifica o token PRINT ("=")
        if (code.current() == '=') {
            return parsePrintToken(code);
        }

        // Depois verifica palavras reservadas alfabéticas
        if (!Character.isLetter(code.current())) {
            return null;
        }

        StringBuilder word = new StringBuilder();
        while (Character.isLetter(code.current())) {
            word.append(code.current());
            code.next();
        }

        String result = word.toString();
        switch (result) {
            case "deus":
                return new Token("DEF_FUNC", "deus");
            case "amen":
                return new Token("RETURN", "amen");
            case "se":
                return new Token("IF", "se");
            case "senao":
                return new Token("ELSE", "senao");
            // Adicione outras palavras reservadas aqui
            default:
                return null;
        }
    }

    private Token parsePrintToken(CharacterIterator code) {
        code.next(); // Consome o '='

        // Verifica se é print com aspas duplas
        if (code.current() != '"') {
            return new Token("ASSIGN", "="); // Caso seja atribuição simples
        }
        code.next(); // Consome a aspa dupla

        StringBuilder content = new StringBuilder();
        boolean isString = (code.current() == '\'');

        // Tratamento para strings (conteúdo entre aspas simples)
        if (isString) {
            code.next(); // Consome a aspa simples inicial
            while (code.current() != '\'' && code.current() != CharacterIterator.DONE) {
                content.append(code.current());
                code.next();
            }
            if (code.current() != '\'') {
                return null; // Erro: aspa simples não fechada
            }
            code.next(); // Consome a aspa simples final
        }
        // Tratamento para identificadores/números
        else {
            while (code.current() != '"' && code.current() != CharacterIterator.DONE) {
                content.append(code.current());
                code.next();
            }
        }

        // Verifica aspa dupla final
        if (code.current() != '"') {
            return null;
        }
        code.next();

        return new Token("PRINT", content.toString());
    }
}