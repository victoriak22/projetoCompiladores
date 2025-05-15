package lexico;


import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import lexico.tokens.AFD;
import lexico.tokens.Delimiters;
import lexico.tokens.EOFToken;
import lexico.tokens.Identifier;
import lexico.tokens.Limiters;
import lexico.tokens.MathOperator;
import lexico.tokens.Number;
import lexico.tokens.PrintToken;
import lexico.tokens.RelationalOperator;
import lexico.tokens.ReservedWords;
import lexico.tokens.StringIdentifier;

import java.util.ArrayList;

public class Lexer {
    private List<Token> tokens;
    private List<AFD> afds;
    private CharacterIterator code;

    public Lexer(String code) {
        tokens = new ArrayList<>();
        afds = new ArrayList<>();
        this.code = new StringCharacterIterator(code.replace("\r\n", "\n")); // Normaliza quebras de linha

        // Ordem de avaliação
        afds.add(new PrintToken());         // Primeiro verifica o print
        afds.add(new ReservedWords());
        afds.add(new Identifier());
        afds.add(new Delimiters());
        afds.add(new MathOperator());
        afds.add(new Number());
        afds.add(new RelationalOperator());
        afds.add(new Limiters());
        afds.add(new StringIdentifier());
        afds.add(new EOFToken());
    }

    // Método para pular espaços em branco e outros caracteres irrelevantes
    public void skipWhiteSpace() {
        while (code.current() == ' ' || code.current() == '\n' || code.current() == '\r' || code.current() == '\t') {
            code.next();
        }
    }

    // Método para obter os tokens do código
    public List<Token> getTokens() {
        Token t;
        do {
            skipWhiteSpace();
            t = searchNextToken();
            if (t == null) {
                // Mostra mais contexto no erro
                int pos = code.getIndex();
                String context = "";
                for (int i = 0; i < 10 && code.current() != CharacterIterator.DONE; i++) {
                    context += code.current();
                    code.next();
                }
                throw new RuntimeException("Erro léxico na posição " + pos + 
                                        ". Contexto: '" + context + "'");
            }
            tokens.add(t);
        } while (!t.getTipo().equals("EOF"));
        return tokens;
    }

    // Procura o próximo token válido
    private Token searchNextToken() {
        int pos = code.getIndex();  // Guarda a posição atual no código
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);  // Tenta encontrar um token com cada AFD
            if (t != null) {
                return t;  // Retorna o token encontrado
            }
            code.setIndex(pos);  // Se não encontrou, volta à posição inicial
        }
        return null;  // Se não encontrou nenhum token válido, retorna null
    }

}

