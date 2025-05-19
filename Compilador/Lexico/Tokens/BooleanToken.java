package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;
import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class BooleanToken extends AFD {
  @Override
  public Token evaluate(CharacterIterator code) {
    int startPos = code.getIndex();
    StringBuilder sb = new StringBuilder();

    // Coleta caracteres que podem formar uma palavra
    while (Character.isLetter(code.current()) && code.current() != CharacterIterator.DONE) {
      sb.append(code.current());
      code.next();
    }

    String palavra = sb.toString();

    // Verifica se é uma das palavras reservadas para booleanos
    if (palavra.equals("luz")) {
      return new Token("BOOLEAN", "luz");
    } else if (palavra.equals("trevas")) {
      return new Token("BOOLEAN", "trevas");
    }

    // Não é um booleano, volta para posição original
    code.setIndex(startPos);
    return null;
  }
}