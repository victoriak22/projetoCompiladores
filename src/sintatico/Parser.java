import java.util.List;

public class Parser {
    List<Token> tokens;
    Token token;

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
            System.out.println("Consumindo token: " + next.getLexema());
            if (!isIgnoravel(next)) {
                System.out.println("Token válido, Lexema: " + next.getLexema() + " Tipo: " + next.getTipo());
                return next;
            }
        }
        return null;
    }

    private boolean isIgnoravel(Token token) {
        String tipo = token.getTipo();
        return tipo.equals("TAB") || tipo.equals("SPACE") || tipo.equals("NEWLINE");
    }

    private void erro(String regra) {
        System.out.println("Erro na regra: " + regra);
        if (token != null)
            System.out.println("Token inválido: " + token.getLexema());
        else
            System.out.println("Token inválido: <null>");
        System.exit(0);
    }

    public void main() {
        token = getNextToken();
        if (token == null)
            erro("main");

        if (listaComandos()) {
            if (token != null && token.getTipo().equals("EOF")) {
                System.out.println("Análise sintática correta");
                return;
            }
        }
        erro("main");
    }

    public boolean listaComandos() {
        System.out.println("Iniciando lista de comandos...");
        while (comando()) {
            // Consome comandos enquanto possível
        }
        System.out.println("Lista de comandos processada");
        return true;
    }

    public boolean comando() {
        System.out.println("Verificando comando...");
        return declaracao() || atribuicao() || estruturaCondicional() ||
                estruturaLoop() || estruturaEscolha() || tratamentoErros() ||
                chamadaFuncao() || comentario();
    }

    public boolean declaracao() {
        System.out.println("Verificando declaração...");
        if (matchL("deus") && matchT("id") && matchL("->") &&
                parametros() && matchL("{") && listaComandos() && matchL("}")) {
            System.out.println("Declaração válida!");
            return true;
        }
        return false;
    }

    public boolean parametros() {
        System.out.println("Verificando parâmetros...");
        if (matchT("id")) {
            while (matchL(",")) {
                if (!matchT("id"))
                    erro("parametros");
            }
            System.out.println("Parâmetros processados com sucesso");
            return true;
        }
        return true; // vazio
    }

    public boolean atribuicao() {
        System.out.println("Verificando atribuição...");
        if ((matchL(":") && matchT("id")) || (matchL("este") && matchL(".") && matchT("id"))) {
            if (matchL("->") && expressao())
                return true;
        }
        return false;
    }

    public boolean expressao() {
        System.out.println("Verificando expressão...");
        if (chamadaFuncao())
            return true;
        if (valor()) {
            while (operador()) {
                if (!expressao())
                    erro("expressao");
            }
            return true;
        }
        return false;
    }

    public boolean valor() {
        System.out.println("Verificando valor...");
        return matchT("num") || matchT("str") || matchT("id") ||
                matchL("luz") || matchL("trevas") || matchL("nulo");
    }

    public boolean operador() {
        System.out.println("Verificando operador...");
        return matchL("+") || matchL("-") || matchL("*") || matchL("/") ||
                matchL("==") || matchL("!=") || matchL(">") || matchL("<") ||
                matchL(">=") || matchL("<=");
    }

    public boolean chamadaFuncao() {
        System.out.println("Verificando chamada de função...");
        if (matchT("id") && matchL("(")) {
            parametros();
            if (!matchL(")"))
                erro("chamadaFuncao");
            return true;
        }
        return false;
    }

    public boolean estruturaCondicional() {
        System.out.println("Verificando estrutura condicional...");
        if (matchL("se") && matchL("(") && expressao() && matchL(")") &&
                matchL(":") && listaComandos()) {

            while (matchL("senaose") && matchL("(") && expressao() && matchL(")") &&
                    matchL(":") && listaComandos()) {
            }

            if (matchL("senao") && matchL(":") && listaComandos()) {
            }

            return true;
        }
        return false;
    }

    public boolean estruturaLoop() {
        System.out.println("Verificando estrutura de loop...");
        if (matchL("loop") && matchL("(") &&
                atribuicao() && matchL(";") &&
                expressao() && matchL(";") &&
                atribuicao() && matchL(")") &&
                matchL("{") && listaComandos() && matchL("}")) {
            return true;
        }

        if (matchL("enquanto") && matchL("(") && expressao() && matchL(")") &&
                matchL("{") && listaComandos() && matchL("}")) {
            return true;
        }

        return false;
    }

    public boolean estruturaEscolha() {
        System.out.println("Verificando estrutura de escolha...");
        if (matchL("escolha") && matchL("(") && expressao() && matchL(")") && matchL("{")) {
            casos();
            padrao();
            if (!matchL("}"))
                erro("estruturaEscolha");
            return true;
        }
        return false;
    }

    public void casos() {
        System.out.println("Verificando casos...");
        while (matchL("caso")) {
            if (!valor())
                erro("caso.valor");
            if (!matchL(":"))
                erro("caso.:");
            listaComandos();
        }
    }

    public void padrao() {
        System.out.println("Verificando padrão...");
        if (matchL("padrao")) {
            if (!matchL(":"))
                erro("padrao.:");
            listaComandos();
        }
    }

    public boolean tratamentoErros() {
        System.out.println("Verificando tratamento de erros...");
        if (matchL("tente") && matchL("{") && listaComandos() && matchL("}") &&
                matchL("capturar") && matchL("(") && matchT("id") && matchL(")") &&
                matchL(":") && listaComandos()) {
            return true;
        }
        return false;
    }

    public boolean comentario() {
        System.out.println("Verificando comentário...");
        return matchL("--") && matchT("str");
    }

    public boolean matchL(String lexema) {
        if (token != null && token.getLexema().equals(lexema)) {
            System.out.println("Token encontrado: " + lexema);
            token = getNextToken();
            return true;
        }
        return false;
    }

    public boolean matchT(String tipo) {
        if (token != null && token.getTipo().equals(tipo)) {
            System.out.println("Token de tipo encontrado: " + tipo);
            token = getNextToken();
            return true;
        }
        return false;
    }
}
