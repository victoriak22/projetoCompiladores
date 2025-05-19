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
            // Funções e retornos
            case "deus":
                return new Token("DEF_FUNC", "deus");
            case "amen":
                return new Token("RETURN", "amen");

            // Estruturas condicionais
            case "se":
                return new Token("IF", "se");
            case "senao":
                return new Token("ELSE", "senao");
            case "senaose":
                return new Token("ELSE_IF", "senaose");

            // Loops e controle de fluxo
            case "loop":
                return new Token("FOR", "loop");
            case "enquanto":
                return new Token("WHILE", "enquanto");
            case "parar":
                return new Token("BREAK", "parar");
            case "continuar":
                return new Token("CONTINUE", "continuar");

            // Tratamento de exceções
            case "tente":
                return new Token("TRY", "tente");
            case "capturar":
                return new Token("CATCH", "capturar");

            // Tipos de dados e valores booleanos
            case "inteiro":
                return new Token("TYPE", "inteiro");
            case "flutuante":
                return new Token("TYPE", "flutuante");
            case "caractere":
                return new Token("TYPE", "caractere");
            case "cadeia":
                return new Token("TYPE", "cadeia");
            case "luz":
                return new Token("BOOLEAN", "luz");
            case "trevas":
                return new Token("BOOLEAN", "trevas");
            case "nulo":
                return new Token("NULL", "nulo");

            // Modificadores e outras palavras-chave
            case "alma":
                return new Token("CLASS", "alma");
            case "publico":
                return new Token("MODIFIER", "publico");
            case "privado":
                return new Token("MODIFIER", "privado");
            case "vazio":
                return new Token("VOID", "vazio");
            case "gen":
                return new Token("NEW", "gen");
            case "este":
                return new Token("THIS", "este");
            case "superior":
                return new Token("SUPER", "superior");
            case "como":
                return new Token("INSTANCEOF", "como");

            // Estrutura escolha-caso
            case "escolha":
                return new Token("SWITCH", "escolha");
            case "caso":
                return new Token("CASE", "caso");
            case "padrao":
                return new Token("DEFAULT", "padrao");

            default:
                return null;
        }
    }

    private Token parsePrintToken(CharacterIterator code) {
        int startPos = code.getIndex();
        code.next(); // Consome o '='

        // Verifica se é print com aspas duplas
        if (code.current() == '"') {
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
                    code.setIndex(startPos); // Retorna ao início para permitir outros AFDs processarem
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
                code.setIndex(startPos); // Retorna ao início para permitir outros AFDs processarem
                return null;
            }
            code.next();

            return new Token("PRINT", content.toString());
        }
        // Verifica se é um operador de comparação
        else if (code.current() == '=') {
            code.next(); // Consome o segundo '='
            return new Token("EQUALS", "=="); // Operador de igualdade
        }
        // Voltar ao início para que outros AFDs possam processar esse token
        code.setIndex(startPos);

        // Este AFD não deve determinar se é atribuição ou não, isso deve ser feito por
        // outro AFD
        return null;
    }
}