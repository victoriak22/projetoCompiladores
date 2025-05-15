package sintatico;


import java.util.List;

import lexico.Token;

public class Parser {
    List<Token> tokens;
    Token token;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private int indentLevel = 0;
    private String getIndent() {
        return "    ".repeat(indentLevel); // 4 espaços por nível
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        // DEPURAÇÃO
        // System.out.println("Lista de tokens recebida:");
        // for (Token t : tokens) {
        //     System.out.println("Token: " + t.getLexema() + ", Tipo: " + t.getTipo());
        // }
    }

    public Token getNextToken() {
        while (!tokens.isEmpty()) {
            Token next = tokens.remove(0);
            if (!isIgnoravel(next)) {
                return next;
            }
        }
        return null;
    }


    private boolean isIgnoravel(Token token) {
        String tipo = token.getTipo();
        return tipo.equals("TAB") || tipo.equals("SPACE") || tipo.equals("NEWLINE");
    }

    private void erro(String mensagem) {
        StringBuilder erro = new StringBuilder();
    
        // Cabeçalho vermelho
        erro.append(ANSI_RED).append("\n--- ERRO SINTÁTICO ---").append(ANSI_RESET).append("\n");
    
        // Mensagem principal (vermelho)
        erro.append(ANSI_RED).append("Mensagem: ").append(ANSI_RESET).append(mensagem).append("\n");
    
        if (token != null) {
            // Detalhes do token (amarelo)
            erro.append(ANSI_YELLOW).append("Token atual: ").append(ANSI_RESET)
                .append(token.getLexema())
                .append(" (Tipo: ").append(token.getTipo()).append(")\n");
        
            // Contexto (ciano)
            erro.append(ANSI_CYAN).append("Contexto: ...");
            for (int i = 0; i < 3 && i < tokens.size(); i++) {
                erro.append(tokens.get(i).getLexema()).append(" ");
            }
            erro.append(ANSI_RESET).append("\n");
        }
    
        // Rodapé vermelho
        erro.append(ANSI_RED).append("----------------------").append(ANSI_RESET);
    
        // Log sem cores adicionais (o método log() já aplicará formatação)
        System.err.println(erro.toString());
        System.exit(1);
    }

    public void main() {
        token = getNextToken();
        if (token == null) {
            erro("Nenhum token encontrado");
            return;
        }

        while (token != null && !token.getTipo().equals("EOF")) {
            if (!listaComandos()) {
                erro("Erro na lista de comandos");
                return;
            }
        }
    
        System.out.println("Análise sintática concluída com sucesso");
    }

    public boolean listaComandos() {
        System.out.println("Iniciando lista de comandos...");
        boolean comandoProcessado = false;

        while (token != null && !token.getTipo().equals("EOF")) {
            int posicaoInicial = tokens.indexOf(token);
            
            if (comando()) {
                comandoProcessado = true;
            } else {
                // Verifica se estamos travados no mesmo token
                if (tokens.indexOf(token) == posicaoInicial) {
                    System.out.println("Token inesperado: " + token.getLexema());
                    avancaToken(); // Força avanço para evitar loop
                }
            }
        }

        return comandoProcessado;
    }

    public boolean comando() {
        System.out.println("Verificando comando...");
        
        // Tenta todos os tipos de comandos (adicione print primeiro)
        if (token.getTipo().equals("PRINT")) {
            avancaToken();
            return true;
        }

        // Consome delimitadores
        if (token.getLexema().equals(",") || token.getLexema().equals(";")) {
            avancaToken();
            return true;
        }

        if (token.getLexema().equals("{")) {
            return bloco();
        }

        if (token.getLexema().equals("}")) {
            return false;
        }
    
        boolean reconhecido = declaracao() || comandoReturn() || atribuicao() || 
                           chamadaFuncao() || chamadaFuncMat() || 
                           estruturaCondicional() || estruturaLoop() || 
                           estruturaEscolha() || tratamentoErros() || 
                           comentario();

        if (!reconhecido) {
            System.out.println("Token não reconhecido como comando: " + token.getLexema());
            avancaToken();
        }
        return reconhecido;
    }


    public boolean declaracao() {
        log(LogLevel.DEBUG, "Iniciando análise de declaração de função");
    
        if (matchL("deus") == null) return false;
    
        Token nomeFuncao = matchT("ID");
        if (nomeFuncao == null || !nomeFuncao.getLexema().startsWith(":")) {
            log(LogLevel.ERROR, "Nome de função inválido: " + (nomeFuncao != null ? nomeFuncao.getLexema() : "null"));
            return false;
        }

        if (matchL("->") == null) {
            log(LogLevel.ERROR, "Esperado '->' após nome da função");
            return false;
        }

        log(LogLevel.INFO, "Declarando função: " + nomeFuncao.getLexema());
        indentLevel++;
    
        if (!parametros()) {
            log(LogLevel.ERROR, "Parâmetros inválidos na função " + nomeFuncao.getLexema());
            return false;
        }

        if (!bloco()) {
            log(LogLevel.ERROR, "Bloco inválido na função " + nomeFuncao.getLexema());
            return false;
        }

        indentLevel--;
        log(LogLevel.INFO, "Função declarada com sucesso: " + nomeFuncao.getLexema());
        return true;
    }

    public boolean comandoReturn() {
        System.out.println("Verificando comando de retorno...");
        if (matchT("RETURN") != null) {
            if (expressao()) {
                System.out.println("Comando de retorno válido.");
                return true;
            } else {
                erro("Esperava uma expressão após 'RETURN'.");
            }
        }
        return false;
    }

    public boolean matchNomeFuncao() {
        if (token == null) return false;
            String tipo = token.getTipo();
            return tipo.equals("id") || tipo.equals("MULTIPLY_FUNC") || tipo.equals("SOMAR_FUNC") || tipo.equals("DIVIDIR_FUNC");
        }

    public boolean parametros() {
        System.out.println("Verificando parâmetros...");

        // Caso 1: Parâmetros na declaração de função (:A, :B)
        if (token != null && token.getTipo().equals("ID") && token.getLexema().startsWith(":")) {
            // Primeiro parâmetro
            Token param = matchT("ID");
            if (param == null) {
                erro("Esperava identificador como parâmetro");
                return false;
            }

            // Parâmetros adicionais
            while (matchL(",") != null) {
                param = matchT("ID");
                if (param == null) {
                    erro("Esperava identificador após vírgula");
                    return false;
                }
            }
            return true;
        }
        // Caso 2: Parâmetros em chamada de função (3, 4)
        else if (matchL("(") != null) {
            if (matchL(")") != null) {
                return true; // Caso sem parâmetros
            }

            do {
                if (!expressao()) {
                    erro("Parâmetro inválido");
                    return false;
                }
            } while (matchL(",") != null);

            if (matchL(")") == null) {
                erro("Esperava ')' ao final dos parâmetros");
                return false;
            }
            return true;
        }
    
        return false;
    }

    public boolean atribuicao() {
        System.out.println("Verificando atribuição...");
        Token id = matchT("ID");
        if (id != null) {
            if (matchL("->") != null && expressao()) {
                System.out.println("Atribuição válida.");
                return true;
            }
        }
        return false;
    }

    public boolean bloco() {
    System.out.println("Verificando bloco...");

    if (!consumirDelimitador("{")) {
        return false;
    }

    while (!token.getLexema().equals("}") && !token.getTipo().equals("EOF")) {
        if (!comando()) {
            erro("Comando inválido no bloco");
            return false;
        }
    }

    if (!consumirDelimitador("}")) {
        erro("Esperava '}' para fechar bloco");
        return false;
    }

    return true;
}


    public boolean expressao() {
        System.out.println("Verificando expressão...");

        // Verifica se é um valor válido (usando a função valor())
        if (!valor()) {
            return false;  // Se não for um valor válido, a expressão não é válida
        }
        // Enquanto houver operadores, tenta consumir o próximo valor
        while (operador()) {
            // Chama recursivamente para processar a próxima parte da expressão
            if (!valor()) {
                erro("Esperava uma expressão após o operador.");
                return false;  // Se não encontrar um valor após o operador, retorna erro
            }
        }
        return true;  // Se a expressão for válida, retorna true
    }

    private boolean valor() {
        // Tenta casar o token com "id" ou "ID"
        Token token = matchT("id");
        if (token == null) token = matchT("ID");

        // Se for um identificador, verifica se é uma chamada de função
        if (token != null) {
            if (peek().getLexema().equals("(")) {  // Verifica se o próximo token é '('
                avancaToken();  // Consome o '('
                // Chamada de função com argumentos
                if (!argumentos()) return false;
            
                // Verifica se o próximo token é ')', caso contrário, retorna erro
                if (!peek().getLexema().equals(")")) {
                    erro("Esperava ')' ao final da chamada de função.");
                    return false;
                }
                avancaToken();  // Consome o token ')'
            }
            return true;  // Se é um ID válido, a expressão é válida
        }
        // Verifica se o token é um número inteiro, decimal ou uma string
        if (matchT("INTEGER") != null) return true;
        if (matchT("DECIMAL") != null) return true;
        if (matchT("STRING") != null) return true;

        return false;  // Caso contrário, retorna falso
    }

    public Token peek() {
        return token;  // Retorna o token atual sem consumir ele.
    }

    private boolean argumentos() {
        // Pode ser vazio
        if (peek() != null && peek().getLexema().equals(")")) return true;

        while (peek() != null) {
            if (!expressao()) {
                erro("Esperava uma expressão válida.");
                return false;
            }

            // Verifica se há uma vírgula para continuar o loop
            if (peek().getLexema().equals(",")) {
                avancaToken();  // Consome a vírgula
            } else if (peek().getLexema().equals(")")) {
                return true;
            } else {
                erro("Token inesperado: " + peek().getLexema());
                return false;
            }
        }
        return true;
    }

    public boolean operador() {
        log(LogLevel.DEBUG, "Verificando operador...");

        return matchL("+") != null ||
               matchL("-") != null ||
               matchL("*") != null ||
               matchL("/") != null ||
               matchL("==") != null ||
               matchL("!=") != null ||
               matchL(">") != null ||
               matchL("<") != null ||
               matchL(">=") != null ||
               matchL("<=") != null;
    }

    public boolean chamadaFuncao() {
        System.out.println("Verificando chamada de função...");
        if (matchT("id") != null && matchL("(") != null) {
            parametros();
            if (matchL(")") == null) {
                erro("Esperava fechar o parêntese em chamada de função.");
            }
            return true;
        }
        return false;
    }

    public boolean estruturaCondicional() {
        System.out.println("Verificando estrutura condicional...");

        if (matchL("se") != null &&
            matchL("(") != null &&
            expressao() &&
            matchL(")") != null &&
            matchL(":") != null &&
            listaComandos()) {
            System.out.println("Bloco 'se' processado.");
            while (matchL("senaose") != null &&
                   matchL("(") != null &&
                   expressao() &&
                   matchL(")") != null &&
                   matchL(":") != null &&
                   listaComandos()) {
                System.out.println("Bloco 'senaose' processado.");
            }
            if (matchL("senao") != null &&
                matchL(":") != null &&
                listaComandos()) {
                System.out.println("Bloco 'senao' processado.");
            }
            return true;
        }
        return false;
    }


    public boolean estruturaLoop() {
        System.out.println("Verificando estrutura de loop...");

        if (matchL("loop") != null &&
            matchL("(") != null &&
            atribuicao() &&
            matchL(";") != null &&
            expressao() &&
            matchL(";") != null &&
            atribuicao() &&
            matchL(")") != null &&
            matchL("{") != null &&
            listaComandos() &&
            matchL("}") != null) {
            return true;
        }
        if (matchL("enquanto") != null &&
            matchL("(") != null &&
            expressao() &&
            matchL(")") != null &&
            matchL("{") != null &&
            listaComandos() &&
            matchL("}") != null) {
            return true;
        }
        return false;
    }


    public boolean estruturaEscolha() {
        System.out.println("Verificando estrutura de escolha...");
        if (matchL("escolha") != null &&
            matchL("(") != null &&
            expressao() &&
            matchL(")") != null &&
            matchL("{") != null) {
            casos();
            padrao();
            if (matchL("}") == null) {
                erro("Esperava '}' para fechar a estrutura escolha.");
            }
            return true;
        }
        return false;
    }

    public void casos() {
        System.out.println("Verificando casos...");
        while (matchL("caso") != null) {
            if (!valor()) {
                erro("caso.valor");
            }
            if (matchL(":") == null) {
                erro("caso.:");
            }
            listaComandos();
        }
    }

    public void padrao() {
        System.out.println("Verificando padrão...");
        if (matchL("padrao") != null) {
            if (matchL(":") == null) {
                erro("padrao.:");
            }
            listaComandos();
        }
    }

    public boolean tratamentoErros() {
        System.out.println("Verificando tratamento de erros...");

        if (matchL("tente") != null) {
            if (matchL("{") != null && listaComandos() && matchL("}") != null) {
                if (matchL("capturar") != null) {
                    if (matchL("(") != null && matchT("id") != null && matchL(")") != null) {
                        if (matchL(":") != null && listaComandos()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean comentario() {
        System.out.println("Verificando comentário...");

        if (matchL("--") != null) {
            Token t = matchT("str");
            return t != null;
        }

        return false;
    }


    public void avancaToken() {
        if (!tokens.isEmpty()) {
            token = tokens.remove(0);  // Atualiza o token atual removendo o primeiro da lista
        } else {
            token = null;  // Caso não haja mais tokens, token se torna null
        }
    }

    public Token matchL(String lexema) {
        if (token != null && token.getLexema().equals(lexema)) {
            Token consumido = token;
            System.out.println("Consumindo token: " + token.getLexema());
            avancaToken();
            return consumido;
        }
        return null;
    }

    public Token matchT(String tipo) {
        if (token != null && token.getTipo().equals(tipo)) {
            Token consumido = token;
            System.out.println("Consumindo token: " + token.getLexema());
            avancaToken();
            return consumido;
        }
        return null;
    }

    public boolean chamadaFuncMat() {
        System.out.println("Verificando chamada de função matemática...");

        if (matchT("MULTIPLY_FUNC") != null) {
            System.out.println("Função multiplicar detectada.");
            return true;
        }
        if (matchT("SOMAR_FUNC") != null) {
            System.out.println("Função somar detectada.");
            return true;
        }
        if (matchT("DIVIDIR_FUNC") != null) {
            System.out.println("Função dividir detectada.");
            return true;
        }

        return false;
    }

    private boolean consumirDelimitador(String delimitador) {
        if (token != null && token.getLexema().equals(delimitador)) {
            System.out.println("Consumindo delimitador: " + delimitador);
            avancaToken();
            return true;
        }
        return false;
    }

    enum LogLevel { DEBUG, INFO, ERROR }
    private LogLevel logLevel = LogLevel.INFO;

    private void log(LogLevel level, String message) {
        if (level.ordinal() >= logLevel.ordinal()) {
            String coloredMessage = message;
        
            switch (level) {
                case ERROR:
                    coloredMessage = ANSI_RED + message + ANSI_RESET;
                    break;
                case INFO:
                    coloredMessage = ANSI_CYAN + message + ANSI_RESET;
                    break;
                case DEBUG:
                    coloredMessage = ANSI_YELLOW + message + ANSI_RESET;
                    break;
            }
        
            System.out.println(getIndent() + coloredMessage);
        }
    }

}
