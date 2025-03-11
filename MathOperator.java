import java.text.CharacterIterator;

public class MathOperator extends AFD {

@Override
public Token evaluate(CharacterIterator code) {
    switch (code.current()) {
        case '+' :
            code.next();
            return new Token("PLUS", "+");           
        case CharacterIterator.DONE:
            code.next();
            return new Token("EOF","EOF");
        default:
            return null;
        }
    }
}