public class Token{

private String tipo;
private String lexema;

public Token(String tipo, String lexema){
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
return "< " + tipo + "," + lexema + " >";
}
}