package Compilador.Sintatico;

import java.util.ArrayList;
import java.util.List;
import Compilador.Lexico.Token;
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
        // System.out.println("Token: " + t.getLexema() + ", Tipo: " + t.getTipo());
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
        StringBuilder sb = new StringBuilder();

        // Cabeçalho do erro com borda estilizada
        sb.append("\n\u001B[31m╔═══════════════════════════════════════════\u001B[0m\n");
        sb.append("\u001B[31m║ \u001B[1;31mERRO SINTÁTICO\u001B[0m \u001B[31m                          ║\u001B[0m\n");
        sb.append("\u001B[31m╠═══════════════════════════════════════════\u001B[0m\n");

        // Mensagem principal
        sb.append("\u001B[31m║ \u001B[0m• \u001B[1mMotivo:\u001B[0m ").append(mensagem).append("\n");

        if (token != null) {
            // Detalhes do token atual
            sb.append("\u001B[31m║ \u001B[0m• \u001B[1mToken:\u001B[0m ")
                    .append("\u001B[33m'").append(token.getLexema()).append("'\u001B[0m")
                    .append(" (Tipo: \u001B[36m").append(token.getTipo()).append("\u001B[0m)\n");

            // Contexto com seta indicando a posição
            sb.append("\u001B[31m║ \u001B[0m• \u001B[1mContexto:\u001B[0m ");
            int contextSize = Math.min(5, tokens.size());
            for (int i = 0; i < contextSize; i++) {
                if (i > 0)
                    sb.append(" ");
                if (i == 0)
                    sb.append("\u001B[7m"); // Destaca o token atual
                sb.append(tokens.get(i).getLexema());
                if (i == 0)
                    sb.append("\u001B[0m");
            }
            sb.append("\n");

            // Sugestões baseadas no tipo de erro
            if (token.getLexema().equals(")")) {
                sb.append("\u001B[31m║ \u001B[0m• \u001B[1mSugestão:\u001B[0m Verifique parênteses balanceados\n");
            } else if (token.getTipo().equals("ID")) {
                sb.append(
                        "\u001B[31m║ \u001B[0m• \u001B[1mSugestão:\u001B[0m Variável não declarada ou uso incorreto\n");
            }
        }

        // Rodapé
        sb.append("\u001B[31m╚═══════════════════════════════════════════\u001B[0m\n\n");

        // Exibe o erro no stderr
        System.err.println(sb.toString());

        // Sai com código de erro padrão para sintaxe inválida
        System.exit(65); // Código de saída padrão para erros de sintaxe
    }

    public ASTNode parse() {
        token = getNextToken();
        List<ASTNode> comandos = new ArrayList<>();

        while (token != null && !token.getTipo().equals("EOF")) {
            ASTNode cmd = comando();
            if (cmd != null) {
                comandos.add(cmd);
            } else {
                avancaToken(); // Avança se não reconhecer o comando
            }
        }

        if (comandos.isEmpty()) {
            erro("Nenhum comando válido encontrado");
            return null;
        }

        return new BlockNode(comandos);
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
        // 1. Comando de impressão
        ASTNode print = comandoPrint();
        if (print != null)
            return print;

        // 1.1 Comando de input (novo)
        ASTNode input = comandoInput();
        if (input != null)
            return input;

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
        if ((cmd = declaracao()) != null)
            return cmd;
        if ((cmd = comandoReturn()) != null)
            return cmd;
        if ((cmd = comandoParar()) != null)
            return cmd;
        if ((cmd = atribuicao()) != null)
            return cmd;
        if ((cmd = chamadaFuncao()) != null)
            return cmd;
        if ((cmd = chamadaFuncMat()) != null)
            return cmd;
        if ((cmd = estruturaCondicional()) != null)
            return cmd;
        if ((cmd = estruturaLoop()) != null)
            return cmd;
        if ((cmd = estruturaEscolha()) != null)
            return cmd;
        if ((cmd = tratamentoErros()) != null)
            return cmd;
        if ((cmd = comentario()) != null)
            return cmd;

        // 6. Token não reconhecido
        if (token != null) {
            System.out.println("Token não reconhecido como comando: " + token.getLexema());
            avancaToken();
        }
        return null;
    }

    /**
     * Processa um comando de entrada (leitura)
     * Formato: $ler(:variavel)
     */
    public ASTNode comandoInput() {
        if (token != null && token.getTipo().equals("INPUT")) {
            // Extrai o nome da variável (entre parênteses)
            String lexema = token.getLexema();
            int inicio = lexema.indexOf("(") + 1;
            int fim = lexema.indexOf(")");

            if (inicio > 0 && fim > inicio) {
                String nomeVar = lexema.substring(inicio, fim);

                // Cria o nó de variável
                VarNode var = new VarNode(nomeVar);

                // Avança o token
                avancaToken();

                // Retorna o nó de input
                return new InputNode(var);
            } else {
                erro("Formato inválido para comando de leitura: " + lexema);
            }
        }
        return null;
    }

    public ASTNode declaracao() {
        // 1. Verifica 'deus'
        if (matchL("deus") == null)
            return null;

        // 2. Obtém nome da função (remove o ':' inicial)
        Token nomeFuncao = matchT("ID");
        if (nomeFuncao == null || !nomeFuncao.getLexema().startsWith(":")) {
            erro("Nome de função inválido. Deve começar com ':'");
            return null;
        }
        String nomeSanitizado = nomeFuncao.getLexema().substring(1); // Remove o ':'

        // 3. Verifica '->'
        if (matchL("->") == null) {
            erro("Esperado '->' após nome da função");
            return null;
        }

        // 4. Coleta parâmetros (com sanitização)
        List<ParamNode> params = new ArrayList<>();
        Token paramToken = matchT("ID");

        if (paramToken != null && paramToken.getLexema().startsWith(":")) {
            params.add(new ParamNode(paramToken.getLexema()));

            while (matchL(",") != null) {
                paramToken = matchT("ID");
                if (paramToken == null || !paramToken.getLexema().startsWith(":")) {
                    erro("Parâmetro inválido após vírgula");
                    return null;
                }
                params.add(new ParamNode(paramToken.getLexema()));
            }
        } else {
            erro("Pelo menos um parâmetro é necessário");
            return null;
        }

        // 5. Obtém corpo
        BlockNode corpo = bloco();
        if (corpo == null) {
            erro("Corpo da função inválido ou faltando");
            return null;
        }

        // 6. Retorna o nó formatado
        return new FunctionDeclNode(
                nomeSanitizado, // Nome SEM ':'
                params, // List<ParamNode>
                corpo // BlockNode
        );
    }

    public ASTNode comandoReturn() {
        if (matchT("RETURN") == null && matchT("amen") == null)
            return null;

        ASTNode expr = expressao();
        if (expr == null) {
            erro("Esperava expressão após RETURN/amen");
            return null;
        }

        return new ReturnNode(expr);
    }

    /**
     * Método para processar o comando "parar" (break)
     */
    public ASTNode comandoParar() {
        if (matchL("parar") != null) {
            return new BreakNode(); // Você precisará criar essa classe
        }
        return null;
    }

    public boolean matchNomeFuncao() {
        if (token == null)
            return false;
        String tipo = token.getTipo();
        return tipo.equals("id") || tipo.equals("MULTIPLY_FUNC") || tipo.equals("SOMAR_FUNC")
                || tipo.equals("DIVIDIR_FUNC");
    }

    // Para chamada de função (ex: `soma(1, 2)`)
    private List<ASTNode> parseArgumentos() {
        List<ASTNode> args = new ArrayList<>();

        // Processa primeiro argumento
        ASTNode expr = expressao();
        if (expr != null) {
            args.add(expr);
        }

        // Processa argumentos adicionais
        while (token != null && token.getLexema().equals(",")) {
            avancaToken(); // Consome ","
            expr = expressao();
            if (expr != null) {
                args.add(expr);
            } else {
                erro("Esperado expressão após ','");
            }
        }

        return args;
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

    /**
     * Versão atualizada de expressão que suporta operadores lógicos
     */
    public ASTNode expressao() {
        ASTNode node = expressaoRelacional();
        if (node == null)
            return null;

        while (token != null && (token.getLexema().equals("&&") || token.getLexema().equals("||"))) {
            String op = token.getLexema();
            avancaToken();
            ASTNode right = expressaoRelacional();
            if (right == null) {
                erro("Esperava expressão após operador " + op);
                return null;
            }
            node = new BinOpNode(node, op, right);
        }
        return node;
    }

    /**
     * Método para expressões relacionais (==, !=, >, <, etc)
     */
    public ASTNode expressaoRelacional() {
        ASTNode node = expressaoAritmetica();
        if (node == null)
            return null;

        if (token != null && (token.getLexema().equals("==") ||
                token.getLexema().equals("!=") ||
                token.getLexema().equals(">") ||
                token.getLexema().equals("<") ||
                token.getLexema().equals(">=") ||
                token.getLexema().equals("<="))) {
            String op = token.getLexema();
            avancaToken();
            ASTNode right = expressaoAritmetica();
            if (right == null) {
                erro("Esperava expressão após operador " + op);
                return null;
            }
            node = new BinOpNode(node, op, right);
        }
        return node;
    }

    /**
     * Método para expressões aritméticas
     */
    public ASTNode expressaoAritmetica() {
        ASTNode node = termo();
        if (node == null)
            return null;

        while (token != null && (token.getLexema().equals("+") || token.getLexema().equals("-"))) {
            String op = token.getLexema();
            avancaToken();
            ASTNode right = termo();
            if (right == null) {
                erro("Esperava expressão após operador " + op);
                return null;
            }
            node = new BinOpNode(node, op, right);
        }
        return node;
    }

    private ASTNode termo() {
        ASTNode node = fator();
        if (node == null)
            return null;

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
        if (token == null)
            return null;

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

    public ASTNode valor() {
        if (token == null)
            return null;

        // Caso 1: Números (inteiros e decimais)
        if (token.getTipo().equals("INTEGER")) {
            Num num = new Num(token.getLexema(), "INTEGER");
            avancaToken();
            return num;
        }

        if (token.getTipo().equals("FLOAT")) {
            Num num = new Num(token.getLexema(), "DECIMAL");
            avancaToken();
            return num;
        }

        // Caso 2: Booleanos
        if (token.getTipo().equals("BOOLEAN")) {
            BooleanNode bool = new BooleanNode(token.getLexema());
            avancaToken();
            return bool;
        }

        // Caso 3: Strings
        if (token.getTipo().equals("STRING")) {
            Str str = new Str(token.getLexema().substring(1, token.getLexema().length() - 1));
            avancaToken();
            return str;
        }

        // Caso 4: Identificadores (variáveis)
        if (token.getTipo().equals("ID")) {
            VarNode var = new VarNode(token.getLexema());
            avancaToken();
            return var;
        }

        return null;
    }

    public Token peek() {
        return token; // Retorna o token atual sem consumir ele.
    }

    private List<ASTNode> argumentos() {
        List<ASTNode> args = new ArrayList<>();

        // Caso 1: Lista vazia de argumentos (ex: "função()")
        if (peek() != null && peek().getLexema().equals(")")) {
            return args;
        }

        // Caso 2: Processa cada argumento
        while (peek() != null) {
            ASTNode expr = expressao();
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
        Token idToken = matchT("ID");
        if (idToken == null || matchL("(") == null)
            return null;

        // Usa parseArgumentos() para argumentos
        List<ASTNode> args = parseArgumentos();
        if (matchL(")") == null)
            return null;

        return new CallExpr(idToken.getLexema(), args);
    }

    public ASTNode estruturaCondicional() {
        // Verificamos se começa com "se"
        if (token == null || !token.getLexema().equals("se")) {
            return null;
        }

        avancaToken(); // Consome "se"

        // Verificamos a condição
        if (token == null || !token.getLexema().equals("(")) {
            erro("Esperava '(' após 'se'");
            return null;
        }
        avancaToken(); // Consome "("

        ASTNode condicao = expressao();
        if (condicao == null) {
            erro("Condição inválida em 'se'");
            return null;
        }

        if (token == null || !token.getLexema().equals(")")) {
            erro("Esperava ')' após condição");
            return null;
        }
        avancaToken(); // Consome ")"

        // Verificamos se temos um bloco
        BlockNode thenBlock = null;
        if (token != null && token.getLexema().equals("{")) {
            // Bloco completo
            thenBlock = bloco();
        } else {
            // Comando único
            ASTNode comando = comando();
            if (comando != null) {
                List<ASTNode> comandos = new ArrayList<>();
                comandos.add(comando);
                thenBlock = new BlockNode(comandos);
            }
        }

        if (thenBlock == null) {
            erro("Esperava bloco ou comando após se(condição)");
            return null;
        }

        // Lista para armazenar blocos "senaose"
        List<ElseIfNode> elseIfBlocks = new ArrayList<>();
        BlockNode elseBlock = null;

        // Processamos blocos "senaose" em sequência
        while (token != null && token.getLexema().equals("senaose")) {
            avancaToken(); // Consome "senaose"

            if (token == null || !token.getLexema().equals("(")) {
                erro("Esperava '(' após 'senaose'");
                return null;
            }
            avancaToken(); // Consome "("

            ASTNode elseIfCond = expressao();
            if (elseIfCond == null) {
                erro("Condição inválida em 'senaose'");
                return null;
            }

            if (token == null || !token.getLexema().equals(")")) {
                erro("Esperava ')' após condição em 'senaose'");
                return null;
            }
            avancaToken(); // Consome ")"

            // Verificamos se temos um bloco
            ASTNode elseIfBody = null;
            if (token != null && token.getLexema().equals("{")) {
                // Bloco completo
                elseIfBody = bloco();
            } else {
                // Comando único
                ASTNode comando = comando();
                if (comando != null) {
                    List<ASTNode> comandos = new ArrayList<>();
                    comandos.add(comando);
                    elseIfBody = new BlockNode(comandos);
                }
            }

            if (elseIfBody == null) {
                erro("Esperava bloco ou comando após senaose(condição)");
                return null;
            }

            elseIfBlocks.add(new ElseIfNode(elseIfCond, (BlockNode) elseIfBody));
        }

        // Processamos um possível bloco "senao"
        if (token != null && token.getLexema().equals("senao")) {
            avancaToken(); // Consome "senao"

            // Verificamos se temos um bloco
            if (token != null && token.getLexema().equals("{")) {
                // Bloco completo
                elseBlock = (BlockNode) bloco();
            } else {
                // Comando único
                ASTNode comando = comando();
                if (comando != null) {
                    List<ASTNode> comandos = new ArrayList<>();
                    comandos.add(comando);
                    elseBlock = new BlockNode(comandos);
                }
            }

            if (elseBlock == null) {
                erro("Esperava bloco ou comando após senao");
                return null;
            }
        }

        return new IfNode(condicao, (BlockNode) thenBlock, elseIfBlocks, elseBlock);
    }

    public ASTNode estruturaLoop() {
        // 1. Loop no estilo for (loop(inicialização; condição; incremento))
        if (matchL("loop") != null) {
            if (matchL("(") == null)
                return null;

            ASTNode inicializacao = atribuicao();
            if (inicializacao == null || matchL(";") == null)
                return null;

            ASTNode condicao = expressao();
            if (condicao == null || matchL(";") == null)
                return null;

            ASTNode incremento = atribuicao();
            if (incremento == null || matchL(")") == null)
                return null;

            if (matchL("{") == null)
                return null;
            BlockNode corpo = listaComandos();
            if (corpo == null || matchL("}") == null)
                return null;

            return new ForLoopNode(inicializacao, condicao, incremento, corpo);
        }

        // 2. Loop no estilo while (enquanto(condição))
        if (matchL("enquanto") != null) {
            if (matchL("(") == null)
                return null;

            ASTNode condicao = expressao();
            if (condicao == null || matchL(")") == null)
                return null;

            if (matchL("{") == null)
                return null;
            BlockNode corpo = listaComandos();
            if (corpo == null || matchL("}") == null)
                return null;

            return new WhileLoopNode(condicao, corpo);
        }

        return null; // Não é um loop
    }

    public ASTNode estruturaEscolha() {
        // 1. Verifica 'escolha (expressão) {'
        if (matchL("escolha") == null)
            return null;
        if (matchL("(") == null)
            return null;

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
                if (valorCaso == null || matchL(":") == null)
                    return null;

                BlockNode corpo = listaComandos();
                casos.add(new CaseNode(valorCaso, corpo));
            } else if (matchL("padrao") != null) {
                if (matchL(":") == null)
                    return null;
                padrao = listaComandos();
                break;
            } else {
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

        if (matchL("tente") == null)
            return null;

        // Bloco TRY
        if (matchL("{") == null)
            return null;
        BlockNode tryBlock = listaComandos();
        if (tryBlock == null || matchL("}") == null)
            return null;

        // Bloco CATCH
        if (matchL("capturar") == null) {
            return new TryNode(tryBlock); // Try sem catch
        }

        if (matchL("(") == null)
            return null;
        Token exceptionVar = matchT("id");
        if (exceptionVar == null || matchL(")") == null || matchL(":") == null) {
            return null;
        }

        BlockNode catchBlock = listaComandos();
        if (catchBlock == null)
            return null;

        return new TryCatchNode(tryBlock, exceptionVar.getLexema(), catchBlock);
    }

    public ASTNode comentario() {
        System.out.println("Verificando comentário...");

        if (matchL("--") == null)
            return null;

        Token comentarioToken = matchT("str");
        if (comentarioToken == null) {
            erro("Esperava texto do comentário após '--'");
            return null;
        }

        return new CommentNode(comentarioToken.getLexema());
    }

    private void avancaToken() {
        if (!tokens.isEmpty()) {
            System.out.println("Consumindo token: " + token.getLexema()); // DEBUG
            token = tokens.remove(0);
        } else {
            token = null;
        }
    }

    public Token matchL(String lexema) {
        if (token != null && token.getLexema().equals(lexema)) {
            Token t = token;
            avancaToken(); // Avança apenas uma vez
            return t;
        }
        return null;
    }

    public Token matchT(String tipo) {
        if (token != null && token.getTipo().equals(tipo)) {
            Token t = token;
            avancaToken(); // Avança apenas uma vez
            return t;
        }
        return null;
    }

    /**
     * Versão atualizada para processar comandos de impressão
     * Suporta tanto o formato antigo "=" quanto o novo "p()"
     */
    public ASTNode comandoPrint() {
        // Verifica formato novo (começando com p)
        if (token != null && token.getTipo().equals("PRINT")) {
            if (token.getLexema().startsWith("p(")) {
                // Nova sintaxe: p("conteúdo") ou p(variavel)
                String lexema = token.getLexema();
                int inicio = lexema.indexOf("(") + 1;
                int fim = lexema.lastIndexOf(")");

                if (inicio > 0 && fim > inicio) {
                    String conteudo = lexema.substring(inicio, fim);
                    ASTNode printContent;

                    // Verifica se é string literal ou variável
                    if (conteudo.startsWith("\"") && conteudo.endsWith("\"")) {
                        // String literal (remove as aspas)
                        printContent = new Str(conteudo.substring(1, conteudo.length() - 1));
                    } else {
                        // Variável ou expressão
                        printContent = new VarNode(conteudo);
                    }

                    // Avança o token
                    avancaToken();

                    return new PrintNode(printContent);
                }
            } else {
                // Formato antigo: ="conteúdo" ou =variavel
                Token printToken = token;
                avancaToken();
                String conteudo = printToken.getLexema().substring(1); // Remove o '='
                ASTNode printContent = new Str(conteudo);

                // Verificar se há concatenação (operador +)
                if (token != null && token.getLexema().equals("+")) {
                    avancaToken(); // Consumir o token "+"

                    // Ler a variável após o operador +
                    ASTNode varNode = valor();
                    if (varNode != null) {
                        // Criar nó de concatenação
                        return new PrintNode(new BinOpNode(printContent, "+", varNode));
                    }
                }

                // Se não houver concatenação, retorna apenas o conteúdo
                return new PrintNode(printContent);
            }
        }
        return null;
    }

    public ASTNode parseExpression() {
        ASTNode node = parseTerm();

        while (token != null && (token.getLexema().equals("+") || token.getLexema().equals("-"))) {
            String op = token.getLexema();
            avancaToken();
            ASTNode right = parseTerm();
            node = new BinOpNode(node, op, right);
        }

        return node;
    }

    private ASTNode parseTerm() {
        ASTNode node = parseFactor();

        while (token != null && (token.getLexema().equals("*") || token.getLexema().equals("/"))) {
            String op = token.getLexema();
            avancaToken();
            ASTNode right = parseFactor();
            node = new BinOpNode(node, op, right);
        }

        return node;
    }

    private ASTNode parseFactor() {
        if (token == null)
            return null;

        // Números inteiros ou decimais
        if (token.getTipo().equals("INTEGER") || token.getTipo().equals("DECIMAL")) {
            Num num = new Num(token.getLexema(), token.getTipo());
            avancaToken();
            return num;
        }

        // Variáveis (começam com :)
        if (token.getTipo().equals("ID") && token.getLexema().startsWith(":")) {
            VarNode var = new VarNode(token.getLexema());
            avancaToken();
            return var;
        }

        // Parênteses para agrupamento
        if (token.getLexema().equals("(")) {
            avancaToken();
            ASTNode expr = parseExpression();
            if (expr == null || !token.getLexema().equals(")")) {
                erro("Esperado ')'");
                return null;
            }
            avancaToken();
            return expr;
        }

        erro("Fator inválido: " + token.getLexema());
        return null;
    }

    public ASTNode chamadaFuncMat() {
        System.out.println("Verificando chamada de função matemática...");

        Token funcToken = matchT("MULTIPLY_FUNC");
        if (funcToken == null)
            funcToken = matchT("SOMAR_FUNC");
        if (funcToken == null)
            funcToken = matchT("DIVIDIR_FUNC");

        if (funcToken == null)
            return null; // Não é uma função matemática

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

    enum LogLevel {
        DEBUG, INFO, ERROR
    }

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

    /**
     * Classe para representar um nó de break (parar) na AST
     */
    public static class BreakNode extends ASTNode {
        @Override
        public String toFormattedString(String indent, boolean isLast) {
            return indent + (isLast ? "└── " : "├── ") + "Break\n";
        }
    }
}