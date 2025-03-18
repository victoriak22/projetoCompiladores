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
                return new Token("PALAVRA_RESERVADA", "se");
            case "senao":
                return new Token("PALAVRA_RESERVADA", "senao");
            case "senaose":
                return new Token("PALAVRA_RESERVADA", "senaose");
            case "para":
                return new Token("PALAVRA_RESERVADA", "para");
            case "enquanto":
                return new Token("PALAVRA_RESERVADA", "enquanto");
            case "parar":
                return new Token("PALAVRA_RESERVADA", "parar");
            case "continuar":
                return new Token("PALAVRA_RESERVADA", "continuar");
            case "amen":
                return new Token("PALAVRA_RESERVADA", "amen");
            case "tente":
                return new Token("PALAVRA_RESERVADA", "tente");
            case "capturar":
                return new Token("PALAVRA_RESERVADA", "capturar");
            case "alma":
                return new Token("PALAVRA_RESERVADA", "alma");
            case "aberto":
                return new Token("PALAVRA_RESERVADA", "aberto");
            case "oculto":
                return new Token("PALAVRA_RESERVADA", "oculto");
            case "vazio":
                return new Token("PALAVRA_RESERVADA", "vazio");
            case "inteiro":
                return new Token("PALAVRA_RESERVADA", "inteiro");
            case "flutuante":
                return new Token("PALAVRA_RESERVADA", "flutuante");
            case "caractere":
                return new Token("PALAVRA_RESERVADA", "caractere");
            case "cadeia":
                return new Token("PALAVRA_RESERVADA", "cadeia");
            case "verdadeiro":
                return new Token("PALAVRA_RESERVADA", "verdadeiro");
            case "falso":
                return new Token("PALAVRA_RESERVADA", "falso");
            case "nulo":
                return new Token("PALAVRA_RESERVADA", "nulo");
            case "manifestar":
                return new Token("PALAVRA_RESERVADA", "manifestar");
            case "este":
                return new Token("PALAVRA_RESERVADA", "este");
            case "superior":
                return new Token("PALAVRA_RESERVADA", "superior");
            case "como":
                return new Token("PALAVRA_RESERVADA", "como");
            case "interromper":
                return new Token("PALAVRA_RESERVADA", "interromper");
            case "mudar":
                return new Token("PALAVRA_RESERVADA", "mudar");
            case "caso":
                return new Token("PALAVRA_RESERVADA", "caso");
            case "padrao":
                return new Token("PALAVRA_RESERVADA", "padrao");
            default:
                return null;
        }
    }
}