import java.util.List;

public class Parser {
    List<Token> tokens;
    Token token;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
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

    private void erro(String regra) {
        System.out.println("Regra: " + regra);
        if (token != null)
            System.out.println("Token inválido: " + token.getLexema());
        else
            System.out.println("Token inválido: <null>");
        System.exit(0);
    }

    public void main() {
        token = getNextToken();
        if (token == null) erro("main");

        if (listaComandos()) {
            if (token != null && token.getTipo().equals("EOF")) {
                System.out.println("sintaticamente correto");
                return;
            }
        }
        erro("main");
    }

    public boolean listaComandos() {
        while (comando()) {
            // Consome comandos enquanto possível
        }
        return true;
    }

    public boolean comando() {
        return declaracao() || atribuicao() || estruturaCondicional() ||
               estruturaLoop() || estruturaEscolha() || tratamentoErros() ||
               chamadaFuncao() || comentario();
    }

    public boolean declaracao() {
        if (matchL("deus") && matchT("id") && matchL("->") &&
            parametros() && matchL("{") && listaComandos() && matchL("}")) {
            return true;
        }
        return false;
    }

    public boolean parametros() {
        if (matchT("id")) {
            while (matchL(",")) {
                if (!matchT("id")) erro("parametros");
            }
            return true;
        }
        return true; // vazio
    }

    public boolean atribuicao() {
        if ((matchL(":") && matchT("id")) || (matchL("este") && matchL(".") && matchT("id"))) {
            if (matchL("->") && expressao()) return true;
        }
        return false;
    }

    public boolean expressao() {
        if (chamadaFuncao()) return true;
        if (valor()) {
            while (operador()) {
                if (!expressao()) erro("expressao");
            }
            return true;
        }
        return false;
    }

    public boolean valor() {
        return matchT("num") || matchT("str") || matchT("id") ||
               matchL("luz") || matchL("trevas") || matchL("nulo");
    }

    public boolean operador() {
        return matchL("+") || matchL("-") || matchL("*") || matchL("/") ||
               matchL("==") || matchL("!=") || matchL(">") || matchL("<") ||
               matchL(">=") || matchL("<=");
    }

    public boolean chamadaFuncao() {
        if (matchT("id") && matchL("(")) {
            parametros();
            if (!matchL(")")) erro("chamadaFuncao");
            return true;
        }
        return false;
    }

    public boolean estruturaCondicional() {
        if (matchL("se") && matchL("(") && expressao() && matchL(")") &&
            matchL(":") && listaComandos()) {

            while (matchL("senaose") && matchL("(") && expressao() && matchL(")") &&
                   matchL(":") && listaComandos()) {}

            if (matchL("senao") && matchL(":") && listaComandos()) {}

            return true;
        }
        return false;
    }

    public boolean estruturaLoop() {
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
        if (matchL("escolha") && matchL("(") && expressao() && matchL(")") && matchL("{")) {
            casos();
            padrao();
            if (!matchL("}")) erro("estruturaEscolha");
            return true;
        }
        return false;
    }

    public void casos() {
        while (matchL("caso")) {
            if (!valor()) erro("caso.valor");
            if (!matchL(":")) erro("caso.:");
            listaComandos();
        }
    }

    public void padrao() {
        if (matchL("padrao")) {
            if (!matchL(":")) erro("padrao.:");
            listaComandos();
        }
    }

    public boolean tratamentoErros() {
        if (matchL("tente") && matchL("{") && listaComandos() && matchL("}") &&
            matchL("capturar") && matchL("(") && matchT("id") && matchL(")") &&
            matchL(":") && listaComandos()) {
            return true;
        }
        return false;
    }

    public boolean comentario() {
        return matchL("--") && matchT("str");
    }

    public boolean matchL(String lexema) {
        if (token != null && token.getLexema().equals(lexema)) {
            token = getNextToken();
            return true;
        }
        return false;
    }

    public boolean matchT(String tipo) {
        if (token != null && token.getTipo().equals(tipo)) {
            token = getNextToken();
            return true;
        }
        return false;
    }
}