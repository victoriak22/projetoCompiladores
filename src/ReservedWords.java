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
            case "if":
                return new Token("PALAVRA_RESERVADA", "if");
            case "then":
                return new Token("PALVRA_RESERVADA", "then");
            case "else":
                return new Token("PALAVRA_RESERVADA", "else");
            case "while":
                return new Token("PALAVRA_RESERVADA", "while");
            case "for":
                return new Token("PALAVRA_RESERVADA", "for");
            case "return":
                return new Token("PALAVRA_RESERVADA", "return");
            case "char":
                return new Token("PALAVRA_RESERVADA", "char");
            default:
                return null;
        }
    }
}
