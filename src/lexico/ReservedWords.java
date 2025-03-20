import java.text.CharacterIterator;

public class ReservedWords extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(code.current())) {
            sb.append(code.current());
            code.next();
        }
        String word = sb.toString();
        switch (word) {
            case "se":
                return new Token("IF", "se");
            case "senao":
                return new Token("ELSE", "senao");
            case "senaose":
                return new Token("ELSEIF", "senaose");
            case "loop":
                return new Token("FOR", "loop");
            case "enquanto":
                return new Token("WHILE", "enquanto");
            case "parar":
                return new Token("STOP", "parar");
            case "continuar":
                return new Token("CONTINUE", "continuar");
            case "amen":
                return new Token("RETURN", "amen");
            case "tente":
                return new Token("TRY", "tente");
            case "capturar":
                return new Token("CATCH", "capturar");
            case "alma":
                return new Token("CLASS", "alma");
            case "publico":
                return new Token("PUBLIC", "publico");
            case "privado":
                return new Token("PRIVATE", "privado");
            case "vazio":
                return new Token("VOID", "vazio");
            case "inteiro":
                return new Token("INT", "inteiro");
            case "flutuante":
                return new Token("FLOAT", "flutuante");
            case "caractere":
                return new Token("CHAR", "caractere");
            case "luz":
                return new Token("TRUE", "luz");
            case "trevas":
                return new Token("FALSE", "trevas");
            case "nulo":
                return new Token("NULL", "nulo");
            case "manifestar":
                return new Token("MANIFEST", "manifestar");
            case "gen":
                return new Token("NEW", "gen");
            case "como":
                return new Token("INSTANCEOF", "como");
            case "este":
                return new Token("THIS", "este");
            case "superior":
                return new Token("SUPERIOR", "superior");
            case "interromper":
                return new Token("BREAK", "interromper");
            case "escolha":
                return new Token("SWITCH", "escolha");
            case "caso":
                return new Token("CASE", "caso");
            case "padrao":
                return new Token("DEFAULT", "padrao");
            case "deus":
                return new Token("FUNCTION", "deus");
            default:
                return null;
        }
    }
}