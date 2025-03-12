import java.text.CharacterIterator;

public class Identifier extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (Character.isLetter(code.current()) || code.current() == '_') {
            String identificador = readIdentifier(code);
            return new Token("ID", identificador);
        }
        return null;
    }

    private String readIdentifier(CharacterIterator code) {
        StringBuilder identificador = new StringBuilder();

        identificador.append(code.current());
        code.next();

        while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
            identificador.append(code.current());
            code.next();
        }

        return identificador.toString();
    }
}
