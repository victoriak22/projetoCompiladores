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
        return tipo.equals("TAB") || tipo.equals("NEWLINE") || tipo.equals("CARRIAGE_RETURN");
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
        if (token == null)
            erro("main");

        if (programa()) {
            if (token != null && token.getTipo().equals("EOF")) {
                System.out.println("sintaticamente correto");
                return;
            }
        }
        erro("main");
    }

    public boolean programa() {
        return listaComandos();
    }

    public boolean listaComandos() {
        while (comando()) {
            // continua analisando comandos
        }
        return true;
    }

    public boolean comando() {
        return declaracao() || atribuicao() || estruturaCondicional() ||
                estruturaLoop() || estruturaEscolha() ||
                tratamentoErros() || chamadaFuncao() ||
                comentario() || comandoAmen();
    }

    public boolean declaracao() {
        if (matchL("deus") && matchT("id") && matchL("->") && parametros() &&
                matchL("{") && listaComandos() && matchL("}"))
            return true;
        return false;
    }

    public boolean parametros() {
        if (matchT("id")) {
            while (matchL(",")) {
                if (!matchT("id"))
                    erro("parametros");
            }
            return true;
        }
        return true; // vazio
    }

    public boolean atribuicao() {
        if ((matchL(":") && matchT("id")) || (matchL("este") && matchL(".") && matchT("id"))) {
            if (matchL("->") && expressao())
                return true;
        }
        return false;
    }

    public boolean estruturaCondicional() {
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
        if (matchL("loop") && matchL("(") && atribuicao() && matchL(";") &&
                expressao() && matchL(";") && atribuicao() && matchL(")") &&
                matchL("{") && listaComandos() && matchL("}"))
            return true;

        if (matchL("enquanto") && matchL("(") && expressao() && matchL(")") &&
                matchL("{") && listaComandos() && matchL("}"))
            return true;

        return false;
    }

    public boolean estruturaEscolha() {
        if (matchL("escolha") && matchL("(") && expressao() && matchL(")") &&
                matchL("{")) {
            while (matchL("caso") && valor() && matchL(":") && listaComandos()) {
            }
            if (matchL("padrao") && matchL(":") && listaComandos()) {
            }
            if (!matchL("}"))
                erro("estruturaEscolha");
            return true;
        }
        return false;
    }

    public boolean tratamentoErros() {
        if (matchL("tente") && matchL("{") && listaComandos() && matchL("}") &&
                matchL("capturar") && matchL("(") && matchT("id") && matchL(")") &&
                matchL(":") && listaComandos())
            return true;
        return false;
    }

    public boolean chamadaFuncao() {
        if (matchT("id") && matchL("(")) {
            parametros();
            if (!matchL(")"))
                erro("chamadaFuncao");
            return true;
        }
        return false;
    }

    public boolean comentario() {
        return matchL("--");
    }

    public boolean comandoAmen() {
        if (matchL("amen") && valor())
            return true;
        return false;
    }

    public boolean expressao() {
        if (!valor())
            return false;
        while (operador()) {
            if (!valor())
                erro("expressao");
        }
        return true;
    }

    public boolean valor() {
        return matchT("id") || matchT("num") || matchT("str") ||
                matchL("luz") || matchL("trevas") || matchL("nulo");
    }

    public boolean operador() {
        return matchL("+") || matchL("-") || matchL("*") || matchL("/") ||
                matchL("==") || matchL("!=") || matchL(">") || matchL("<") ||
                matchL(">=") || matchL("<=");
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