import java.util.List;

public class Parser{
    List<Token> tokens;
    Token token;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public Token getNextToken(){
        if(tokens.size() > 0)
            return tokens.remove(0);
        return null;
    }
   
    private void erro(String regra){
        System.out.println("Regra: " + regra);
        System.out.println("Token inválido: " + token.getLexema());
        System.exit(0);
    }

    void main(){
        token = getNextToken();
        if (ifelse()){
            if (token.getTipo().equals("EOF")){
                System.out.println("sintaticamente correto");
                return;
            }
        }
        erro("main");
    }

    public boolean ifelse(){
        if(matchL("if") && condicao() && matchL("then") && expressao() && matchL("else") && expressao())
            return true;
    
        return false;
}
    public boolean condicao(){
        if (id() && operador() && num()){
            return true;
        }
        return false;
    }

    public boolean id(){
        return matchT("id");
    }

    public boolean num(){
        return matchT("num");
    }

    public boolean operador(){
        if(matchL(">") || matchL("<") || matchL("==")){
            return true;
        }
        return false;
    }

    public boolean expressao(){
        if(matchT("id") && matchL("=") && matchT("num")){
            return true;
        }
        return false;
    }

    public boolean matchL(String lexema){
        if (token.getLexema().equals(lexema)){
            token = getNextToken();
            return true;
        }
        return false;
    }

    public boolean matchT(String tipo){
        if (token.getTipo().equals(tipo)){
            token = getNextToken();
            return true;       }
        return false;
    }
}
