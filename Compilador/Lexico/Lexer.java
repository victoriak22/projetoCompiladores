package Compilador.Lexico;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.ArrayList;
import Compilador.Lexico.Tokens.*;
import Compilador.Lexico.Tokens.Number;

public class Lexer {
    private List<Token> tokens;
    private List<AFD> afds;
    private CharacterIterator code;
    private int linha = 1;
    private int coluna = 1;

    public Lexer(String code) {
        tokens = new ArrayList<>();
        afds = new ArrayList<>();
        this.code = new StringCharacterIterator(code.replace("\r\n", "\n"));

        // Ordem de avaliação é crítica - do mais específico ao mais genérico
        afds.add(new CommentToken()); // Comentários
        afds.add(new PrintToken()); // Prints
        afds.add(new StringToken()); // Strings entre aspas simples
        afds.add(new BooleanToken()); // Valores booleanos
        afds.add(new ReservedWords());
        afds.add(new RelationalOperator()); // ==, !=, etc
        afds.add(new MathOperator()); // +, -, *, /
        afds.add(new Number()); // Números inteiros/decimais
        afds.add(new Delimiters()); // , ; ( ) { }
        afds.add(new Identifier()); // Identificadores (começam com :)

        afds.add(new EOFToken()); // Fim de arquivo
    }

    public List<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();

        while (true) {
            skipWhiteSpace();

            Token token = searchNextToken();
            if (token == null) {
                if (code.current() == CharacterIterator.DONE) {
                    Token eofToken = new Token("EOF", "");
                    eofToken.setPosicao(linha, coluna);
                    tokens.add(eofToken);
                    break;
                }
                throw criarErroLexico();
            }

            token.setPosicao(linha, coluna);

            // Comentários são ignorados e não adicionados à lista de tokens
            if (!token.getTipo().equals("COMMENT")) {
                tokens.add(token);
            }

            atualizarPosicao(token.getLexema());

            if (token.getTipo().equals("EOF")) {
                break;
            }
        }

        return tokens;
    }

    public class StringToken extends AFD {
        @Override
        public Token evaluate(CharacterIterator code) {
            if (code.current() == '\'') {
                StringBuilder sb = new StringBuilder();
                sb.append(code.current());
                code.next();

                while (code.current() != '\'' && code.current() != CharacterIterator.DONE) {
                    sb.append(code.current());
                    code.next();
                }

                if (code.current() == '\'') {
                    sb.append(code.current());
                    code.next();
                    return new Token("STRING", sb.toString());
                }
            }
            return null;
        }
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(code.current())) {
            if (code.current() == '\n') {
                linha++;
                coluna = 1;
            } else {
                coluna++;
            }
            code.next();
        }
    }

    private void atualizarPosicao(String lexema) {
        int quebrasDeLinha = contarQuebrasDeLinha(lexema);
        if (quebrasDeLinha > 0) {
            linha += quebrasDeLinha;
            coluna = lexema.length() - lexema.lastIndexOf('\n') - 1;
        } else {
            coluna += lexema.length();
        }
    }

    private int contarQuebrasDeLinha(String texto) {
        int count = 0;
        for (char c : texto.toCharArray()) {
            if (c == '\n')
                count++;
        }
        return count;
    }

    private RuntimeException criarErroLexico() {
        StringBuilder contexto = new StringBuilder();
        int posInicial = code.getIndex();

        // Captura 10 caracteres antes e depois
        code.setIndex(Math.max(0, posInicial - 10));
        for (int i = 0; i < 20 && code.current() != CharacterIterator.DONE; i++) {
            contexto.append(code.current());
            code.next();
        }

        return new RuntimeException(String.format(
                "Erro léxico na linha %d, coluna %d.\n" +
                        "Contexto: '%.30s'\n" +
                        "          ^ Caractere inesperado: '%s'",
                linha, coluna, contexto.toString(),
                code.current() == CharacterIterator.DONE ? "EOF" : String.valueOf(code.current())));
    }

    private Token searchNextToken() {
        int pos = code.getIndex();
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);
            if (t != null) {
                return t;
            }
            code.setIndex(pos); // Reset para próxima tentativa
        }
        return null;
    }
}