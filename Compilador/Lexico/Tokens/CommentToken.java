package Compilador.Lexico.Tokens;

import java.text.CharacterIterator;

import Compilador.Lexico.AFD;
import Compilador.Lexico.Token;

public class CommentToken extends AFD {
  @Override
  public Token evaluate(CharacterIterator code) {
    // Verifica se começa com --
    if (code.current() == '-') {
      int startPos = code.getIndex();
      code.next();

      if (code.current() == '-') {
        StringBuilder sb = new StringBuilder("--");
        code.next(); // Pula o segundo '-'

        // Consome todos os caracteres até encontrar uma quebra de linha ou fim do
        // arquivo
        while (code.current() != '\n' && code.current() != CharacterIterator.DONE) {
          sb.append(code.current());
          code.next();
        }

        // Retorna o token de comentário
        return new Token("COMMENT", sb.toString());
      } else {
        // Se não for um comentário, volta para a posição original
        code.setIndex(startPos);
      }
    }
    return null;
  }
}