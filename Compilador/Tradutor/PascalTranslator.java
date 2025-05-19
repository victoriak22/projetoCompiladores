package Compilador.Tradutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Compilador.ast.*;
import Compilador.ast.Comandos.*;
import Compilador.ast.Expressoes.*;
import Compilador.ast.Expressoes.Variaveis.*;

/**
 * Tradutor compacto e eficiente que converte AST da linguagem PSALMS para
 * código Pascal.
 */
public class PascalTranslator {

  /**
   * Traduz a AST para código Pascal.
   */
  public static String translate(ASTNode ast) {
    StringBuilder code = new StringBuilder();
    StringBuilder functions = new StringBuilder();
    StringBuilder mainCode = new StringBuilder();
    Set<String> variables = new HashSet<>();

    // Primeira passagem: identificar variáveis e funções
    collectDeclarations(ast, variables, null);

    // Processar ast para separar funções do código principal
    processAST(ast, functions, mainCode);

    // Cabeçalho do programa Pascal
    code.append("PROGRAM PsalmsProgram;\n\n");

    // Seção de variáveis
    code.append("VAR\n");
    for (String variable : variables) {
      code.append("  ").append(variable).append(": Integer;\n");
    }
    code.append("\n");

    // Declarações de funções (antes do BEGIN principal)
    code.append(functions);

    // Programa principal
    code.append("BEGIN\n");
    code.append(mainCode);
    code.append("END.\n");

    return code.toString();
  }

  /**
   * Coleta declarações de variáveis e funções.
   */
  private static void collectDeclarations(ASTNode node, Set<String> variables, Set<String> functions) {
    if (node == null)
      return;

    try {
      if (node instanceof BlockNode) {
        Field stmtsField = BlockNode.class.getDeclaredField("statements");
        stmtsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

        for (ASTNode stmt : statements) {
          collectDeclarations(stmt, variables, functions);
        }
      } else if (node instanceof AssignNode) {
        Field lhsField = AssignNode.class.getDeclaredField("lhs");
        lhsField.setAccessible(true);
        ASTNode lhs = (ASTNode) lhsField.get(node);

        if (lhs instanceof VarNode) {
          Method getName = VarNode.class.getMethod("getName");
          String varName = normalize((String) getName.invoke(lhs));
          variables.add(varName);
        }

        // Verificar lado direito também (pode conter variáveis)
        Field rhsField = AssignNode.class.getDeclaredField("rhs");
        rhsField.setAccessible(true);
        ASTNode rhs = (ASTNode) rhsField.get(node);
        collectDeclarations(rhs, variables, functions);
      } else if (node instanceof FunctionDeclNode) {
        if (functions != null) {
          Field nameField = FunctionDeclNode.class.getDeclaredField("name");
          nameField.setAccessible(true);
          String name = normalize((String) nameField.get(node));
          functions.add(name);
        }

        // Coletar variáveis locais dentro da função
        Field bodyField = FunctionDeclNode.class.getDeclaredField("body");
        bodyField.setAccessible(true);
        BlockNode body = (BlockNode) bodyField.get(node);
        collectDeclarations(body, variables, functions);
      }
      // Outros nós que podem conter variáveis (Ex: BinOpNode, CallExpr, etc.)
    } catch (Exception e) {
      // Ignorar erros na coleta
    }
  }

  /**
   * Processa a AST, separando funções do código principal.
   */
  private static void processAST(ASTNode node, StringBuilder functions, StringBuilder mainCode) {
    if (node == null)
      return;

    try {
      if (node instanceof BlockNode) {
        Field stmtsField = BlockNode.class.getDeclaredField("statements");
        stmtsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

        for (ASTNode stmt : statements) {
          if (stmt instanceof FunctionDeclNode) {
            // Função vai para a seção de funções
            translateNode(stmt, functions, 0);
          } else {
            // Código normal vai para o corpo principal
            translateNode(stmt, mainCode, 1);
          }
        }
      } else {
        // Caso seja um nó que não é um bloco, traduzimos para o corpo principal
        translateNode(node, mainCode, 1);
      }
    } catch (Exception e) {
      mainCode.append("  { Erro ao processar AST: ").append(e.getMessage()).append(" }\n");
    }
  }

  /**
   * Traduz um nó da AST recursivamente.
   */
  private static void translateNode(ASTNode node, StringBuilder code, int indent) {
    if (node == null)
      return;

    // Aplicar indentação
    String indentation = "  ".repeat(indent);
    try {

      if (node instanceof BlockNode) {
        // Bloco de comandos
        Field stmtsField = BlockNode.class.getDeclaredField("statements");
        stmtsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

        for (ASTNode stmt : statements) {
          translateNode(stmt, code, indent);
        }
      } else if (node instanceof FunctionDeclNode) {
        // Declaração de função
        Field nameField = FunctionDeclNode.class.getDeclaredField("name");
        nameField.setAccessible(true);
        String name = normalize((String) nameField.get(node));

        Field paramsField = FunctionDeclNode.class.getDeclaredField("params");
        paramsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ParamNode> params = (List<ParamNode>) paramsField.get(node);

        Field bodyField = FunctionDeclNode.class.getDeclaredField("body");
        bodyField.setAccessible(true);
        BlockNode body = (BlockNode) bodyField.get(node);

        // Verificar se tem return para determinar se é função ou procedimento
        boolean hasReturn = false;
        Field stmtsField = BlockNode.class.getDeclaredField("statements");
        stmtsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> statements = (List<ASTNode>) stmtsField.get(body);

        for (ASTNode stmt : statements) {
          if (stmt instanceof ReturnNode) {
            hasReturn = true;
            break;
          }
        }

        // Declaração da função/procedimento
        code.append(hasReturn ? "FUNCTION " : "PROCEDURE ").append(name).append("(");

        // Parâmetros
        for (int i = 0; i < params.size(); i++) {
          Method getName = ParamNode.class.getMethod("getName");
          String paramName = normalize((String) getName.invoke(params.get(i)));
          code.append(paramName).append(": Integer");
          if (i < params.size() - 1)
            code.append("; ");
        }
        code.append(")");

        if (hasReturn)
          code.append(": Integer");
        code.append(";\n");

        // Variáveis locais (se houver)
        Set<String> localVars = new HashSet<>();
        collectLocalVars(body, localVars);

        if (!localVars.isEmpty()) {
          code.append("VAR\n");
          for (String var : localVars) {
            code.append("  ").append(var).append(": Integer;\n");
          }
        }

        // Corpo da função
        code.append("BEGIN\n");
        translateNode(body, code, 1);
        code.append("END;\n\n");
      } else if (node instanceof AssignNode) {
        // Atribuição (a := b)
        Field lhsField = AssignNode.class.getDeclaredField("lhs");
        lhsField.setAccessible(true);
        ASTNode lhs = (ASTNode) lhsField.get(node);

        Field rhsField = AssignNode.class.getDeclaredField("rhs");
        rhsField.setAccessible(true);
        ASTNode rhs = (ASTNode) rhsField.get(node);

        code.append(indentation);

        if (lhs instanceof VarNode) {
          Method getName = VarNode.class.getMethod("getName");
          String varName = normalize((String) getName.invoke(lhs));
          code.append(varName);
        } else {
          translateNode(lhs, code, 0); // Sem indentação extra
        }

        code.append(" := ");
        translateNode(rhs, code, 0); // Sem indentação extra
        code.append(";\n");
      } else if (node instanceof ReturnNode) {
        // Return (amen) -> Result := valor
        Field exprField = ReturnNode.class.getDeclaredField("expression");
        exprField.setAccessible(true);
        ASTNode expr = (ASTNode) exprField.get(node);

        code.append(indentation).append("Result := ");
        translateNode(expr, code, 0);
        code.append(";\n");
      } else if (node instanceof CallExpr) {
        // Chamada de função
        Field nameField = CallExpr.class.getDeclaredField("functionName");
        nameField.setAccessible(true);
        String funcName = normalize((String) nameField.get(node));

        Field argsField = CallExpr.class.getDeclaredField("args");
        argsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> args = (List<ASTNode>) argsField.get(node);

        code.append(funcName).append("(");

        for (int i = 0; i < args.size(); i++) {
          translateNode(args.get(i), code, 0);
          if (i < args.size() - 1)
            code.append(", ");
        }

        code.append(")");
      } else if (node instanceof IfNode) {
        // If-Then-Else
        Field condField = IfNode.class.getDeclaredField("condicao");
        condField.setAccessible(true);
        ASTNode cond = (ASTNode) condField.get(node);

        Field thenField = IfNode.class.getDeclaredField("thenBlock");
        thenField.setAccessible(true);
        BlockNode thenBlock = (BlockNode) thenField.get(node);

        Field elseField = IfNode.class.getDeclaredField("elseBlock");
        elseField.setAccessible(true);
        BlockNode elseBlock = (BlockNode) elseField.get(node);

        code.append(indentation).append("IF ");
        translateNode(cond, code, 0);
        code.append(" THEN\n").append(indentation).append("BEGIN\n");

        translateNode(thenBlock, code, indent + 1);

        code.append(indentation).append("END");

        if (elseBlock != null) {
          code.append("\n").append(indentation).append("ELSE\n").append(indentation).append("BEGIN\n");
          translateNode(elseBlock, code, indent + 1);
          code.append(indentation).append("END");
        }

        code.append(";\n");
      } else if (node instanceof WhileLoopNode) {
        // While loop
        Field condField = WhileLoopNode.class.getDeclaredField("condicao");
        condField.setAccessible(true);
        ASTNode cond = (ASTNode) condField.get(node);

        Field bodyField = WhileLoopNode.class.getDeclaredField("corpo");
        bodyField.setAccessible(true);
        BlockNode body = (BlockNode) bodyField.get(node);

        code.append(indentation).append("WHILE ");
        translateNode(cond, code, 0);
        code.append(" DO\n").append(indentation).append("BEGIN\n");

        translateNode(body, code, indent + 1);

        code.append(indentation).append("END;\n");
      } else if (node instanceof PrintNode) {
        // Print (="...") -> WriteLn
        Field contentField = PrintNode.class.getDeclaredField("conteudo");
        contentField.setAccessible(true);
        ASTNode content = (ASTNode) contentField.get(node);

        code.append(indentation).append("WriteLn(");

        // Verificar se é uma operação BinOp de concatenação com +
        if (content instanceof BinOpNode) {
          Method getOpMethod = BinOpNode.class.getMethod("getOperator");
          String op = (String) getOpMethod.invoke(content);

          if (op.equals("+")) {
            // Transformar concatenação com + em argumentos separados por vírgula
            Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
            ASTNode left = (ASTNode) getLeftMethod.invoke(content);

            Method getRightMethod = BinOpNode.class.getMethod("getRight");
            ASTNode right = (ASTNode) getRightMethod.invoke(content);

            translateNode(left, code, 0);
            code.append(", ");
            translateNode(right, code, 0);
          } else {
            // Outro tipo de operação, manter como está
            translateNode(content, code, 0);
          }
        } else {
          translateNode(content, code, 0);
        }

        code.append(");\n");
      } else if (node instanceof BinOpNode) {
        // Operador binário
        Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
        ASTNode left = (ASTNode) getLeftMethod.invoke(node);

        Method getOpMethod = BinOpNode.class.getMethod("getOperator");
        String op = (String) getOpMethod.invoke(node);

        Method getRightMethod = BinOpNode.class.getMethod("getRight");
        ASTNode right = (ASTNode) getRightMethod.invoke(node);

        // Converter operadores para Pascal
        String pascalOp = op;
        if (op.equals("=="))
          pascalOp = "=";
        else if (op.equals("!="))
          pascalOp = "<>";
        else if (op.equals("%"))
          pascalOp = "MOD";

        code.append("(");
        translateNode(left, code, 0);
        code.append(" ").append(pascalOp).append(" ");
        translateNode(right, code, 0);
        code.append(")");
      } else if (node instanceof VarNode) {
        // Variável
        Method getName = VarNode.class.getMethod("getName");
        String varName = normalize((String) getName.invoke(node));
        code.append(varName);
      } else if (node instanceof Num) {
        // Número
        Method getValor = Num.class.getMethod("getValor");
        String valor = (String) getValor.invoke(node);
        code.append(valor);
      } else if (node instanceof Str) {
        // String
        Method getValor = Str.class.getMethod("getValor");
        String valor = (String) getValor.invoke(node);

        // Em Pascal, strings usam aspas simples e duplicam aspas internas
        code.append("'").append(valor.replace("'", "''")).append("'");
      } else if (node instanceof ForLoopNode) {
        // For loop -> adaptado para while em Pascal
        Field inicField = ForLoopNode.class.getDeclaredField("inicializacao");
        inicField.setAccessible(true);
        ASTNode inic = (ASTNode) inicField.get(node);

        Field condField = ForLoopNode.class.getDeclaredField("condicao");
        condField.setAccessible(true);
        ASTNode cond = (ASTNode) condField.get(node);

        Field incField = ForLoopNode.class.getDeclaredField("incremento");
        incField.setAccessible(true);
        ASTNode inc = (ASTNode) incField.get(node);

        Field bodyField = ForLoopNode.class.getDeclaredField("corpo");
        bodyField.setAccessible(true);
        BlockNode body = (BlockNode) bodyField.get(node);

        // Inicialização
        translateNode(inic, code, indent);

        // While com condição
        code.append(indentation).append("WHILE ");
        translateNode(cond, code, 0);
        code.append(" DO\n").append(indentation).append("BEGIN\n");

        // Corpo do loop
        translateNode(body, code, indent + 1);

        // Incremento
        translateNode(inc, code, indent + 1);

        code.append(indentation).append("END;\n");
      } else if (node instanceof SwitchNode) {
        // Switch -> Case em Pascal
        Field exprField = SwitchNode.class.getDeclaredField("expressao");
        exprField.setAccessible(true);
        ASTNode expr = (ASTNode) exprField.get(node);

        Field casosField = SwitchNode.class.getDeclaredField("casos");
        casosField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CaseNode> casos = (List<CaseNode>) casosField.get(node);

        Field defaultField = SwitchNode.class.getDeclaredField("padrao");
        defaultField.setAccessible(true);
        ASTNode padrao = (ASTNode) defaultField.get(node);

        code.append(indentation).append("CASE ");
        translateNode(expr, code, 0);
        code.append(" OF\n");

        // Casos individuais
        for (CaseNode caso : casos) {
          Field valorField = CaseNode.class.getDeclaredField("valor");
          valorField.setAccessible(true);
          ASTNode valor = (ASTNode) valorField.get(caso);

          Field corpoField = CaseNode.class.getDeclaredField("corpo");
          corpoField.setAccessible(true);
          ASTNode corpo = (ASTNode) corpoField.get(caso);

          code.append(indentation).append("  ");
          translateNode(valor, code, 0);
          code.append(": BEGIN\n");

          translateNode(corpo, code, indent + 2);

          code.append(indentation).append("  END;\n");
        }

        // Caso padrão (default)
        if (padrao != null) {
          code.append(indentation).append("  ELSE BEGIN\n");
          translateNode(padrao, code, indent + 2);
          code.append(indentation).append("  END;\n");
        }

        code.append(indentation).append("END;\n");
      }
      // Outros nós podem ser adicionados conforme necessário

    } catch (Exception e) {
      code.append(indentation).append("{ Erro: ").append(e.getMessage()).append(" }\n");
    }
  }

  /**
   * Coleta variáveis locais de um bloco.
   */
  private static void collectLocalVars(BlockNode node, Set<String> localVars) {
    try {
      Field stmtsField = BlockNode.class.getDeclaredField("statements");
      stmtsField.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

      for (ASTNode stmt : statements) {
        if (stmt instanceof AssignNode) {
          Field lhsField = AssignNode.class.getDeclaredField("lhs");
          lhsField.setAccessible(true);
          ASTNode lhs = (ASTNode) lhsField.get(stmt);

          if (lhs instanceof VarNode) {
            Method getName = VarNode.class.getMethod("getName");
            String varName = normalize((String) getName.invoke(lhs));
            if (!varName.equals("Result")) { // "Result" é palavra reservada em Pascal
              localVars.add(varName);
            }
          }
        }
      }
    } catch (Exception e) {
      // Ignorar erros
    }
  }

  /**
   * Remove ":" do início dos identificadores PSALMS para adequar ao Pascal.
   */
  private static String normalize(String id) {
    return id.startsWith(":") ? id.substring(1) : id;
  }
}