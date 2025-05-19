package Compilador.ast.Expressoes;

import Compilador.ast.ASTNode;

public class BooleanNode extends ASTNode {
  private final boolean valor;
  private final String lexema;

  public BooleanNode(String lexema) {
    this.lexema = lexema;
    this.valor = lexema.equals("luz");
  }

  @Override
  public String toFormattedString(String indent, boolean isLast) {
    return indent + (isLast ? "└── " : "├── ") + "Boolean: " + lexema + "\n";
  }

  @Override
  public String toString() {
    return toFormattedString("", true);
  }

  public boolean getValor() {
    return valor;
  }

  public String getLexema() {
    return lexema;
  }
}