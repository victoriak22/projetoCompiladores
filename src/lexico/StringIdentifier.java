import java.text.CharacterIterator;

public class StringIdentifier extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == '"' || code.current() == '\'' || code.current() == '\"') {
            char quote = code.current();
            StringBuilder value = new StringBuilder();
            code.next();
            
            while (code.current() != CharacterIterator.DONE && code.current() != quote) {
                value.append(code.current());
                code.next();
            }

            if (code.current() == quote) {
                code.next();
                return new Token("STRING", value.toString());
            }
        }
        return null;
    }
}
