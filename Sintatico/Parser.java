package Compilador;

import java.util.ArrayList;
import java.util.List;

import Compilador.ast.ASTNode;
import Compilador.ast.Expressoes.*;
import Compilador.ast.Expressoes.Variaveis.ParamNode;
import Compilador.ast.Expressoes.Variaveis.VarNode;
import Compilador.ast.Comandos.*;




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

    public ASTNode parse() {  // Renomeamos para 'parse' para refletir melhor a função
        token = getNextToken();
        if (token == null) {
            erro("Nenhum token encontrado");
            return null;
        }

        List<ASTNode> comandos = new ArrayList<>();
        while (token != null && !token.getTipo().equals("EOF")) {
            ASTNode cmd = comando();
            if (cmd != null) {
                comandos.add(cmd);
            } else if (token != null) {  // Avança se houver erro não crítico
                avancaToken();
            }
        }

        if (!comandos.isEmpty()) {
            System.out.println("\n✅ Análise sintática concluída com sucesso");
            System.out.println("\nÁrvore Sintática Abstrata (AST):");
            for (ASTNode cmd : comandos) {
                System.out.println(cmd.toString(0));
            }
            return new BlockNode(comandos);  // Retorna o bloco raiz
        } else {
            erro("Nenhum comando válido encontrado");
            return null;
        }
    }

    public BlockNode listaComandos() {
        List<ASTNode> comandos = new ArrayList<>();
        ASTNode cmd;
        while ((cmd = comando()) != null) {
            comandos.add(cmd);
        }
        return new BlockNode(comandos);
    }

    public ASTNode comando() {
        // 1. Comando PRINT
        if (token != null && token.getTipo().equals("PRINT")) {
            avancaToken();
            ASTNode expr = expressao();
            if (expr == null) {
                erro("Esperava expressão após PRINT");
                return null;
            }
            return new PrintNode(expr);
        }

        // 2. Delimitadores (opcional)
        if (token != null && (token.getLexema().equals(",") || token.getLexema().equals(";"))) {
            avancaToken();
            return new EmptyNode();
        }

        // 3. Bloco { ... }
        if (token != null && token.getLexema().equals("{")) {
            return bloco();
        }

        // 4. Fim de bloco
        if (token != null && token.getLexema().equals("}")) {
            return null; // O bloco quem trata o '}'
        }

        // 5. Comandos complexos (testa cada possibilidade)
        ASTNode cmd;
        if ((cmd = declaracao()) != null) return cmd;
        if ((cmd = comandoReturn()) != null) return cmd;
        if ((cmd = atribuicao()) != null) return cmd;
        if ((cmd = chamadaFuncao()) != null) return cmd;
        if ((cmd = chamadaFuncMat()) != null) return cmd;
        if ((cmd = estruturaCondicional()) != null) return cmd;
        if ((cmd = estruturaLoop()) != null) return cmd;
        if ((cmd = estruturaEscolha()) != null) return cmd;
        if ((cmd = tratamentoErros()) != null) return cmd;
        if ((cmd = comentario()) != null) return cmd;
        // 6. Token não reconhecido
        if (token != null) {
            System.out.println("Token não reconhecido como comando: " + token.getLexema());
            avancaToken();
        }
        return null;
    }


    public ASTNode declaracao() {
        // 1. Verifica a palavra-chave 'deus'
        if (matchL("deus") == null) return null;
    
        // 2. Pega o nome da função (ex: ":minhaFuncao")
        Token nomeFuncao = matchT("ID");
        if (nomeFuncao == null || !nomeFuncao.getLexema().startsWith(":")) {
            erro("Nome de função inválido");
            return null;
        }

        // 3. Verifica o símbolo '->'
        if (matchL("->") == null) {
            erro("Esperado '->' após nome da função");
            return null;
        }

        // 4. Obtém os parâmetros (agora retorna List<ASTNode> de ParamNode)
        List<ASTNode> parametros = parametros();
        if (parametros == null) return null; // Se houver erro

        // 5. Obtém o corpo da função
        ASTNode corpo = bloco();
        if (corpo == null) return null;

        // 6. Cria o nó de declaração de função
        return new FunctionDeclNode(nomeFuncao.getLexema(), parametros, corpo);
    }

    public ASTNode comandoReturn() {
        if (matchT("RETURN") == null && matchT("amen") == null) return null;
    
        ASTNode expr = expressao();
        if (expr == null) {
            erro("Esperava expressão após RETURN/amen");
            return null;
        }
    
        return new ReturnNode(expr);
    }

    public boolean matchNomeFuncao() {
        if (token == null) return false;
            String tipo = token.getTipo();
            return tipo.equals("id") || tipo.equals("MULTIPLY_FUNC") || tipo.equals("SOMAR_FUNC") || tipo.equals("DIVIDIR_FUNC");
        }

    
    public List<ASTNode> parametros() {
        List<ASTNode> params = new ArrayList<>(); // Nome da variável: params

        // Caso 1: Parâmetros formais (declaração de função)
        if (token != null && token.getTipo().equals("ID") && token.getLexema().startsWith(":")) {
            // Primeiro parâmetro
            Token paramToken = matchT("ID");
            if (paramToken == null) {
                erro("Esperava identificador como parâmetro");
                return null;
            }
            params.add(new ParamNode(paramToken.getLexema()));

            // Parâmetros adicionais
            while (matchL(",") != null) {
                paramToken = matchT("ID");
                if (paramToken == null) {
                    erro("Esperava identificador após vírgula");
                    return null;
                }
                params.add(new ParamNode(paramToken.getLexema()));
            }
            return params;
        }
        // Caso 2: Argumentos em chamada de função (2+3, x)
        else if (matchL("(") != null) {
            if (matchL(")") != null) {
                return params; // Lista vazia (agora usando 'params')
            }

            do {
                ASTNode expr = expressao();
                if (expr == null) {
                    erro("Argumento inválido");
                    return null;
                }
                params.add(expr); // Adiciona expressão à lista
            } while (matchL(",") != null);

            if (matchL(")") == null) {
                erro("Esperava ')' ao final dos argumentos");
                return null;
            }
            return params; // Retorna a lista de argumentos
        }

        return null; // Não é uma lista de parâmetros
    }

    public ASTNode atribuicao() {
        Token idToken = matchT("ID");
        if (idToken != null && matchL("->") != null) {
            ASTNode expr = expressao();
            if (expr != null) {
                return new AssignNode(new VarNode(idToken.getLexema()), expr);
            }
        }
        return null;
    }

    public BlockNode bloco() {
        if (!consumirDelimitador("{")) {
            return null;
        }

        List<ASTNode> commands = new ArrayList<>();
        while (token != null && !token.getLexema().equals("}")) {
            ASTNode cmd = comando();
            if (cmd != null) {
                commands.add(cmd);
            } else {
                erro("Comando inválido no bloco");
                return null;
            }
        }

        if (!consumirDelimitador("}")) {
            erro("Esperava '}' para fechar bloco");
            return null;
        }

        return new BlockNode(commands);
    }


    public ASTNode expressao() {
    ASTNode node = termo();
    if (node == null) return null;

    while (token != null && (token.getLexema().equals("+") || token.getLexema().equals("-"))) {
        Token op = token;
        avancaToken();
        ASTNode right = termo();
        if (right == null) {
            erro("Esperava termo após operador " + op.getLexema());
            return null;
        }
        node = new BinOpNode(node, op.getLexema(), right);
    }
    return node;
}

    private ASTNode termo() {
        ASTNode node = fator();
        if (node == null) return null;

        while (token != null && (token.getLexema().equals("*") || token.getLexema().equals("/"))) {
            Token op = token;
            avancaToken();
            ASTNode right = fator();
            if (right == null) {
                erro("Esperava fator após operador " + op.getLexema());
                return null;
            }
            node = new BinOpNode(node, op.getLexema(), right);
        }
        return node;
    }

    private ASTNode fator() {
        if (token == null) return null;

        // Verifica parênteses
        if (token.getLexema().equals("(")) {
            avancaToken();
            ASTNode node = expressao();
            if (node == null || !token.getLexema().equals(")")) {
                erro("Esperava ')' após expressão");
                return null;
            }
            avancaToken();
            return node;
        }

        // Verifica valores atômicos (números, variáveis, etc.)
        return valor(); // Seu método existente que retorna Num, Var, etc.
    }

// private boolean isOperator(Token t) {
//     return t != null && List.of("+", "-", "*", "/", "==", "<", ">").contains(t.getLexema());
// }

    public ASTNode valor() {
        if (token == null) return null;  // Verificação de segurança

        // Caso 1: Identificador (variável ou chamada de função)
        Token idToken = matchT("id");
        System.out.println("Processando valor. Token atual: " + 
                  (token != null ? token.getLexema() : "null"));
        if (idToken == null) idToken = matchT("ID");
    
        if (idToken != null) {
            // Guarda o lexema antes de avançar os tokens
            String lexema = idToken.getLexema(); 
        
            // Subcaso 1.1: Chamada de função (ex: "soma(2, 3)")
            if (peek() != null && "(".equals(peek().getLexema())) {
                avancaToken(); // Consome '('

                List<ASTNode> args = argumentos();
                if (args == null) return null; // Erro nos argumentos

                if (!expect(")")) {  // Método auxiliar que mostra erro automaticamente
                    return null;
                }
                return new CallExpr(lexema, args);
            }
            // Subcaso 1.2: Variável simples (ex: ":x")
            return new VarNode(lexema);
        }

        // Caso 2: Números e strings
        Token numToken = matchT("INTEGER");
        if (numToken != null) {
            return new Num(numToken.getLexema(), "INTEGER");
        }
    
        numToken = matchT("DECIMAL");
        if (numToken != null) {
            return new Num(numToken.getLexema(), "DECIMAL");
        }
    
        Token strToken = matchT("STRING");
        if (strToken != null) {
            return new Str(strToken.getLexema());
        }

        

        return null; // Token inválido
    }

    private boolean expect(String lexema) {
    if (matchL(lexema) == null) {
        erro("Esperava: " + lexema);
        return false;
    }
    return true;
    }

    public Token peek() {
        return token;  // Retorna o token atual sem consumir ele.
    }

    private List<ASTNode> argumentos() {
        List<ASTNode> args = new ArrayList<>();
    
        // Caso 1: Lista vazia de argumentos (ex: "função()")
        if (peek() != null && peek().getLexema().equals(")")) {
            return args;
        }

        // Caso 2: Processa cada argumento
        while (peek() != null) {
            ASTNode expr = expressao(); // Agora expressao() retorna ASTNode
            if (expr == null) {
                erro("Esperava uma expressão válida.");
                return null;
            }
            args.add(expr);

            // Verifica se há mais argumentos
            if (peek().getLexema().equals(",")) {
                avancaToken(); // Consome a vírgula
            } else if (peek().getLexema().equals(")")) {
                break; // Fim dos argumentos
            } else {
                erro("Token inesperado: " + peek().getLexema());
                return null;
            }
        }
    
        return args;
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

    public ASTNode chamadaFuncao() {
        System.out.println("Verificando chamada de função...");
    
        Token idToken = matchT("id");
        if (idToken == null) idToken = matchT("ID");
        if (idToken == null || matchL("(") == null) {
            return null; // Não é uma chamada de função
        }

        List<ASTNode> args = parametros(); // Agora retorna List<ASTNode>
        if (args == null) {
            return null; // Erro nos parâmetros
        }

        if (matchL(")") == null) {
            erro("Esperava ')' ao final da chamada de função");
            return null;
        }

        return new CallExpr(idToken.getLexema(), args);
    }

    public ASTNode estruturaCondicional() {
        if (matchL("se") == null) return null;

        if (matchL("(") == null) return null;
        ASTNode condicao = expressao();
        if (condicao == null || matchL(")") == null || matchL(":") == null) {
            return null;
        }

        BlockNode thenBlock = listaComandos(); // Agora retorna BlockNode
        List<ElseIfNode> elseIfBlocks = new ArrayList<>();
        BlockNode elseBlock = null;

        while (matchL("senaose") != null) {
            if (matchL("(") == null) return null;
            ASTNode elseIfCond = expressao();
            if (elseIfCond == null || matchL(")") == null || matchL(":") == null) {
                return null;
            }
            BlockNode elseIfBody = listaComandos();
            elseIfBlocks.add(new ElseIfNode(elseIfCond, elseIfBody));
        }

        if (matchL("senao") != null) {
            if (matchL(":") == null) return null;
            elseBlock = listaComandos();
        }

        return new IfNode(condicao, thenBlock, elseIfBlocks, elseBlock);
    }


    public ASTNode estruturaLoop() {
        // 1. Loop no estilo for (loop(inicialização; condição; incremento))
        if (matchL("loop") != null) {
            if (matchL("(") == null) return null;
        
            ASTNode inicializacao = atribuicao();
            if (inicializacao == null || matchL(";") == null) return null;
        
            ASTNode condicao = expressao();
            if (condicao == null || matchL(";") == null) return null;
        
            ASTNode incremento = atribuicao();
            if (incremento == null || matchL(")") == null) return null;
        
            if (matchL("{") == null) return null;
            BlockNode corpo = listaComandos();
            if (corpo == null || matchL("}") == null) return null;
        
            return new ForLoopNode(inicializacao, condicao, incremento, corpo);
        }
    
        // 2. Loop no estilo while (enquanto(condição))
        if (matchL("enquanto") != null) {
            if (matchL("(") == null) return null;
        
            ASTNode condicao = expressao();
            if (condicao == null || matchL(")") == null) return null;
        
            if (matchL("{") == null) return null;
            BlockNode corpo = listaComandos();
            if (corpo == null || matchL("}") == null) return null;
        
            return new WhileLoopNode(condicao, corpo);
        }
    
        return null; // Não é um loop
    }


    public ASTNode estruturaEscolha() {
    // 1. Verifica 'escolha (expressão) {'
    if (matchL("escolha") == null) return null;
    if (matchL("(") == null) return null;
    
    ASTNode expr = expressao();
    if (expr == null || matchL(")") == null || matchL("{") == null) {
        return null;
    }

    // 2. Processa os casos (agora usando valor() internamente)
    List<CaseNode> casos = new ArrayList<>();
    ASTNode padrao = null;
    
    while (true) {
        if (matchL("caso") != null) {
            // Usa valor() para pegar o valor do caso
            ASTNode valorCaso = valor();
            if (valorCaso == null || matchL(":") == null) return null;
            
            BlockNode corpo = listaComandos();
            casos.add(new CaseNode(valorCaso, corpo));
        } 
        else if (matchL("padrao") != null) {
            if (matchL(":") == null) return null;
            padrao = listaComandos();
            break;
        } 
        else {
            break;
        }
    }

    // 3. Fechamento
    if (matchL("}") == null) {
        erro("Esperava '}'");
        return null;
    }

    return new SwitchNode(expr, casos, padrao);
}

public List<CaseNode> processarCasos() {
    List<CaseNode> casos = new ArrayList<>();
    
    while (matchL("caso") != null) {
        // 1. Processa o valor do caso
        ASTNode valorCaso = valor();
        if (valorCaso == null) {
            erro("Valor do caso inválido");
            return null;
        }
        
        // 2. Verifica os dois pontos
        if (matchL(":") == null) {
            erro("Esperava ':' após valor do caso");
            return null;
        }
        
        // 3. Processa o corpo do caso
        BlockNode corpoCaso = listaComandos();
        if (corpoCaso == null) {
            return null; // Erro já tratado em listaComandos()
        }
        
        casos.add(new CaseNode(valorCaso, corpoCaso));
    }
    
    return casos;
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

    public ASTNode tratamentoErros() {
        System.out.println("Verificando tratamento de erros...");

        if (matchL("tente") == null) return null;

        // Bloco TRY
        if (matchL("{") == null) return null;
        BlockNode tryBlock = listaComandos();
        if (tryBlock == null || matchL("}") == null) return null;

        // Bloco CATCH
        if (matchL("capturar") == null) {
            return new TryNode(tryBlock); // Try sem catch
        }

        if (matchL("(") == null) return null;
        Token exceptionVar = matchT("id");
        if (exceptionVar == null || matchL(")") == null || matchL(":") == null) {
            return null;
        }

        BlockNode catchBlock = listaComandos();
        if (catchBlock == null) return null;

        return new TryCatchNode(tryBlock, exceptionVar.getLexema(), catchBlock);
    }

    public ASTNode comentario() {
        System.out.println("Verificando comentário...");

        if (matchL("--") == null) return null;

        Token comentarioToken = matchT("str");
        if (comentarioToken == null) {
            erro("Esperava texto do comentário após '--'");
            return null;
        }

        return new CommentNode(comentarioToken.getLexema());
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

    public ASTNode chamadaFuncMat() {
        System.out.println("Verificando chamada de função matemática...");

        Token funcToken = matchT("MULTIPLY_FUNC");
        if (funcToken == null) funcToken = matchT("SOMAR_FUNC");
        if (funcToken == null) funcToken = matchT("DIVIDIR_FUNC");
    
        if (funcToken == null) return null; // Não é uma função matemática

        if (matchL("(") == null) {
            erro("Esperava '(' após função matemática");
            return null;
        }

        List<ASTNode> args = argumentos(); // argumentos() deve retornar List<ASTNode>
        if (args == null || matchL(")") == null) {
            erro("Argumentos inválidos na função matemática");
            return null;
        }

        return new MathFuncNode(funcToken.getLexema(), args);
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
