package lexico;


public class Token{

    private String tipo;
    private String lexema;

    public Token(String tipo, String lexema){
        if (tipo == null || lexema == null) {
            throw new IllegalArgumentException("Tipo e Lexema não podem ser nulos.");
        }
        if (tipo.isEmpty() || lexema.isEmpty()) {
            throw new IllegalArgumentException("Tipo e Lexema não podem ser vazios.");
        }
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public String getLexema(){
        return lexema;
    }

    public String getTipo(){
        return tipo;
    }

    @Override
    public String toString(){
        return "< " + tipo + ", " + lexema + " >";
    }
}