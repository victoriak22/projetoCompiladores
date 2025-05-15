package lexico.tokens;


import java.text.CharacterIterator;

import lexico.Token;

public abstract class AFD {
    public abstract Token evaluate(CharacterIterator code);
}