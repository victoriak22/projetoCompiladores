package Compilador.Tradutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Compilador.ast.*;
import Compilador.ast.Comandos.*;
import Compilador.ast.Expressoes.*;
import Compilador.ast.Expressoes.Variaveis.*;

/**
 * Tradutor que converte AST da linguagem PSALMS para código Pascal executável.
 */
public class PascalTranslator {
  // Variável para rastrear o nome da função atual sendo processada
  private static String currentFunctionName = "";

  // Mapa para rastrear os tipos das variáveis
  private static Map<String, String> variableTypes = new HashMap<>();

  /**
   * Traduz a AST para código Pascal.
   */
  public static String translate(ASTNode ast) {
    StringBuilder code = new StringBuilder();
    StringBuilder functions = new StringBuilder();
    StringBuilder mainCode = new StringBuilder();
    Set<String> variables = new HashSet<>();
    variableTypes.clear(); // Limpa o mapa de tipos antes de iniciar
    currentFunctionName = ""; // Reset ao iniciar a tradução

    // Primeira passagem: identificar variáveis e funções
    collectDeclarations(ast, variables, null);

    // Processar ast para separar funções do código principal
    processAST(ast, functions, mainCode);

    // Cabeçalho do programa Pascal
    code.append("PROGRAM PsalmsProgram;\n\n");

    // Importar bibliotecas necessárias
    code.append("USES\n  SysUtils; // Para conversões String <-> número\n\n");

    // Seção de variáveis
    if (!variables.isEmpty()) {
      code.append("VAR\n");
      for (String variable : variables) {
        // Determinar tipo baseado no mapa de tipos
        String pascalType = getPascalType(variable);
        code.append("  ").append(variable).append(": ").append(pascalType).append(";\n");
      }
      code.append("\n");
    }

    // Declarações de funções (antes do BEGIN principal)
    code.append(functions);

    // Programa principal
    code.append("BEGIN\n");
    code.append(mainCode);
    code.append("END.\n");

    return code.toString();
  }

  /**
   * Determina o tipo Pascal apropriado baseado no tipo PSALMS
   */
  private static String getPascalType(String variable) {
    String type = variableTypes.getOrDefault(variable, "Unknown");

    switch (type) {
      case "INTEGER":
        return "Integer";
      case "DECIMAL":
        return "Double";
      case "BOOLEAN":
        return "Boolean";
      case "STRING":
        return "String";
      default:
        return "Variant"; // Tipo genérico quando não conseguimos determinar
    }
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

        Field rhsField = AssignNode.class.getDeclaredField("rhs");
        rhsField.setAccessible(true);
        ASTNode rhs = (ASTNode) rhsField.get(node);

        if (lhs instanceof VarNode) {
          Method getName = VarNode.class.getMethod("getName");
          String varName = normalize((String) getName.invoke(lhs));
          variables.add(varName);

          // Inferir tipo baseado no valor atribuído
          inferType(varName, rhs);
        }

        // Verificar lado direito também (pode conter variáveis)
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
    } catch (Exception e) {
      // Ignorar erros na coleta
    }
  }

  /**
   * Infere o tipo de uma variável baseado no valor atribuído
   */
  private static void inferType(String varName, ASTNode valueNode) {
    try {
      if (valueNode instanceof Num) {
        Method getTipo = Num.class.getMethod("getTipo");
        Object tipoEnum = getTipo.invoke(valueNode);
        String tipoStr = tipoEnum.toString();
        variableTypes.put(varName, tipoStr);
      } else if (valueNode instanceof BooleanNode) {
        variableTypes.put(varName, "BOOLEAN");
      } else if (valueNode instanceof Str) {
        variableTypes.put(varName, "STRING");
      } else if (valueNode instanceof BinOpNode) {
        // Para expressões complexas, precisamos analisar os operandos
        Method getOpMethod = BinOpNode.class.getMethod("getOperator");
        String op = (String) getOpMethod.invoke(valueNode);

        if ("+".equals(op)) {
          Method getLeft = BinOpNode.class.getMethod("getLeft");
          Method getRight = BinOpNode.class.getMethod("getRight");
          ASTNode left = (ASTNode) getLeft.invoke(valueNode);
          ASTNode right = (ASTNode) getRight.invoke(valueNode);

          // Se qualquer um dos lados for string, o resultado será string
          if ((left instanceof Str) || (right instanceof Str)) {
            variableTypes.put(varName, "STRING");
          } else {
            // Para outros casos, assumimos decimal para operações numéricas
            variableTypes.put(varName, "DECIMAL");
          }
        } else {
          // Outros operadores como -, *, / geralmente resultam em números
          variableTypes.put(varName, "DECIMAL");
        }
      } else if (valueNode instanceof CallExpr) {
        // Para chamadas de função, poderíamos implementar uma análise de retorno
        // Por enquanto, assumimos Variant
        variableTypes.put(varName, "Unknown");
      }
    } catch (Exception e) {
      // Em caso de erro, assumimos tipo desconhecido
      variableTypes.put(varName, "Unknown");
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

        // Armazenar o nome da função atual
        currentFunctionName = name;

        Field paramsField = FunctionDeclNode.class.getDeclaredField("params");
        paramsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ParamNode> params = (List<ParamNode>) paramsField.get(node);

        Field bodyField = FunctionDeclNode.class.getDeclaredField("body");
        bodyField.setAccessible(true);
        BlockNode body = (BlockNode) bodyField.get(node);

        // Verificar se tem return para determinar se é função ou procedimento
        boolean hasReturn = hasReturnNode(body);

        // Declaração da função/procedimento
        code.append(hasReturn ? "FUNCTION " : "PROCEDURE ").append(name).append("(");

        // Parâmetros
        for (int i = 0; i < params.size(); i++) {
          Method getName = ParamNode.class.getMethod("getName");
          String paramName = normalize((String) getName.invoke(params.get(i)));
          code.append(paramName).append(": Variant"); // Por padrão, usamos Variant para parâmetros
          if (i < params.size() - 1)
            code.append("; ");
        }
        code.append(")");

        if (hasReturn)
          code.append(": Variant"); // Funções podem retornar qualquer tipo
        code.append(";\n");

        // Variáveis locais (se houver)
        Set<String> localVars = new HashSet<>();
        collectLocalVars(body, localVars);

        if (!localVars.isEmpty()) {
          code.append("VAR\n");
          for (String var : localVars) {
            String pascalType = getPascalType(var);
            code.append("  ").append(var).append(": ").append(pascalType).append(";\n");
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
          translateExpressionNode(lhs, code); // Sem indentação extra
        }

        code.append(" := ");
        translateExpressionNode(rhs, code); // Sem indentação extra
        code.append(";\n");
      } else if (node instanceof ReturnNode) {
        // Return (amen) -> nome_da_função := valor
        Field exprField = ReturnNode.class.getDeclaredField("expression");
        exprField.setAccessible(true);
        ASTNode expr = (ASTNode) exprField.get(node);

        code.append(indentation).append(currentFunctionName).append(" := ");
        translateExpressionNode(expr, code);
        code.append(";\n");
      } else if (node instanceof PrintNode) {
        // Print (="...") -> WriteLn
        Field contentField = PrintNode.class.getDeclaredField("conteudo");
        contentField.setAccessible(true);
        ASTNode content = (ASTNode) contentField.get(node);

        code.append(indentation).append("WriteLn(");

        // Traduzir o conteúdo do print com tratamento especial para concatenação
        translatePrintContent(content, code);

        code.append(");\n");
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
        translateExpressionNode(cond, code);
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
        translateExpressionNode(cond, code);
        code.append(" DO\n").append(indentation).append("BEGIN\n");

        translateNode(body, code, indent + 1);

        code.append(indentation).append("END;\n");
      } else if (node instanceof ForLoopNode) {
        // For loop -> While em Pascal
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

        // Inicialização antes do loop
        translateNode(inic, code, indent);

        // Loop While com condição
        code.append(indentation).append("WHILE ");
        translateExpressionNode(cond, code);
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
        translateExpressionNode(expr, code);
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
          translateExpressionNode(valor, code);
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
      } else if (node instanceof CallExpr) {
        // Chamada de função como expressão não precisa de ponto e vírgula
        Field nameField = CallExpr.class.getDeclaredField("functionName");
        nameField.setAccessible(true);
        String funcName = normalize((String) nameField.get(node));

        Field argsField = CallExpr.class.getDeclaredField("args");
        argsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> args = (List<ASTNode>) argsField.get(node);

        code.append(funcName).append("(");

        for (int i = 0; i < args.size(); i++) {
          translateExpressionNode(args.get(i), code);
          if (i < args.size() - 1)
            code.append(", ");
        }

        code.append(")");
      }
    } catch (Exception e) {
      code.append(indentation).append("{ Erro: ").append(e.getMessage()).append(" }\n");
    }
  }

  /**
   * Processa o conteúdo de um nó Print, incluindo concatenações
   */
  private static void translatePrintContent(ASTNode node, StringBuilder code) {
    try {
      if (node instanceof BinOpNode) {
        // Se for operação binária, pode ser concatenação
        Method getOpMethod = BinOpNode.class.getMethod("getOperator");
        String op = (String) getOpMethod.invoke(node);

        if ("+".equals(op)) {
          // É uma concatenação
          Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
          ASTNode left = (ASTNode) getLeftMethod.invoke(node);

          Method getRightMethod = BinOpNode.class.getMethod("getRight");
          ASTNode right = (ASTNode) getRightMethod.invoke(node);

          // Em Pascal, concatenamos com múltiplos parâmetros separados por vírgula
          translatePrintExpression(left, code);
          code.append(", ");
          translatePrintExpression(right, code);
        } else {
          // Outro tipo de operação
          translateExpressionNode(node, code);
        }
      } else if (node instanceof Str) {
        // String direta
        Method getValor = Str.class.getMethod("getValor");
        String valor = (String) getValor.invoke(node);
        code.append("'").append(valor.replace("'", "''")).append("'");
      } else {
        // Outro tipo de nó
        translateExpressionNode(node, code);
      }
    } catch (Exception e) {
      code.append("{ Erro ao processar print: ").append(e.getMessage()).append(" }");
    }
  }

  /**
   * Traduz uma expressão para impressão, aplicando conversões necessárias
   */
  private static void translatePrintExpression(ASTNode node, StringBuilder code) {
    try {
      if (node instanceof Str) {
        // String literal
        Method getValor = Str.class.getMethod("getValor");
        String valor = (String) getValor.invoke(node);
        code.append("'").append(valor.replace("'", "''")).append("'");
      } else if (node instanceof VarNode) {
        // Variável - pode precisar de conversão
        Method getName = VarNode.class.getMethod("getName");
        String varName = normalize((String) getName.invoke(node));

        // Verificar tipo da variável para possível conversão
        String type = variableTypes.getOrDefault(varName, "Unknown");

        switch (type) {
          case "INTEGER":
            code.append("IntToStr(").append(varName).append(")");
            break;
          case "DECIMAL":
            code.append("FloatToStr(").append(varName).append(")");
            break;
          case "BOOLEAN":
            code.append("BoolToStr(").append(varName).append(", 'luz', 'trevas')");
            break;
          case "STRING":
            code.append(varName);
            break;
          default:
            // Se não sabemos o tipo, usamos VarToStr para segurança
            code.append("VarToStr(").append(varName).append(")");
        }
      } else if (node instanceof Num) {
        // Número - precisa ser convertido para string
        Method getTipo = Num.class.getMethod("getTipo");
        Object tipoEnum = getTipo.invoke(node);
        String tipoStr = tipoEnum.toString();

        Method getValor = Num.class.getMethod("getValor");
        String valor = (String) getValor.invoke(node);

        if ("INTEGER".equals(tipoStr)) {
          code.append("IntToStr(").append(valor).append(")");
        } else {
          code.append("FloatToStr(").append(valor).append(")");
        }
      } else if (node instanceof BooleanNode) {
        // Booleano
        Method getLexema = BooleanNode.class.getMethod("getLexema");
        String lexema = (String) getLexema.invoke(node);

        if ("luz".equals(lexema)) {
          code.append("'true'");
        } else {
          code.append("'false'");
        }
      } else if (node instanceof BinOpNode) {
        // Para operações binárias, recursivamente processamos
        translatePrintContent(node, code);
      } else {
        // Para outros tipos, tentamos a tradução regular
        translateExpressionNode(node, code);
      }
    } catch (Exception e) {
      code.append("{ Erro na expressão print: ").append(e.getMessage()).append(" }");
    }
  }

  // Métodos auxiliares para manipular expressões

  private static void translateExpressionNode(ASTNode node, StringBuilder code) {
    try {
      if (node instanceof VarNode) {
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
      } else if (node instanceof BooleanNode) {
        // Valor booleano
        Method getLexema = BooleanNode.class.getMethod("getLexema");
        String lexema = (String) getLexema.invoke(node);
        if ("luz".equals(lexema)) {
          code.append("True");
        } else {
          code.append("False");
        }
      } else if (node instanceof BinOpNode) {
        // Operador binário
        Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
        ASTNode left = (ASTNode) getLeftMethod.invoke(node);

        Method getOpMethod = BinOpNode.class.getMethod("getOperator");
        String op = (String) getOpMethod.invoke(node);

        Method getRightMethod = BinOpNode.class.getMethod("getRight");
        ASTNode right = (ASTNode) getRightMethod.invoke(node);

        // Verificar se é uma concatenação de strings
        boolean isStringConcatenation = isStringExpression(left) || isStringExpression(right);

        if ("+".equals(op) && isStringConcatenation) {
          // Concatenação de strings
          if (isStringExpression(left)) {
            translateExpressionNode(left, code);
          } else {
            // Converter número/booleano para string
            applyStringConversion(left, code);
          }

          code.append(" + "); // Pascal usa + para concatenar strings

          if (isStringExpression(right)) {
            translateExpressionNode(right, code);
          } else {
            // Converter número/booleano para string
            applyStringConversion(right, code);
          }
        } else {
          // Converter operadores para Pascal
          String pascalOp = op;
          if (op.equals("=="))
            pascalOp = "=";
          else if (op.equals("!="))
            pascalOp = "<>";
          else if (op.equals("%"))
            pascalOp = "MOD";

          code.append("(");
          translateExpressionNode(left, code);
          code.append(" ").append(pascalOp).append(" ");
          translateExpressionNode(right, code);
          code.append(")");
        }
      } else if (node instanceof CallExpr) {
        // Chamada de função como parte de expressão
        Field nameField = CallExpr.class.getDeclaredField("functionName");
        nameField.setAccessible(true);
        String funcName = normalize((String) nameField.get(node));

        Field argsField = CallExpr.class.getDeclaredField("args");
        argsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ASTNode> args = (List<ASTNode>) argsField.get(node);

        code.append(funcName).append("(");

        for (int i = 0; i < args.size(); i++) {
          translateExpressionNode(args.get(i), code);
          if (i < args.size() - 1)
            code.append(", ");
        }

        code.append(")");
      }
    } catch (Exception e) {
      code.append("{ Erro na expressão: ").append(e.getMessage()).append(" }");
    }
  }

  /**
   * Verifica se um nó representa uma expressão string
   */
  private static boolean isStringExpression(ASTNode node) {
    try {
      if (node instanceof Str) {
        return true;
      } else if (node instanceof VarNode) {
        Method getName = VarNode.class.getMethod("getName");
        String varName = normalize((String) getName.invoke(node));
        return "STRING".equals(variableTypes.getOrDefault(varName, ""));
      } else if (node instanceof BinOpNode) {
        Method getOpMethod = BinOpNode.class.getMethod("getOperator");
        String op = (String) getOpMethod.invoke(node);

        if ("+".equals(op)) {
          Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
          Method getRightMethod = BinOpNode.class.getMethod("getRight");
          ASTNode left = (ASTNode) getLeftMethod.invoke(node);
          ASTNode right = (ASTNode) getRightMethod.invoke(node);

          return isStringExpression(left) || isStringExpression(right);
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Aplica a conversão necessária para transformar um valor em string
   */
  private static void applyStringConversion(ASTNode node, StringBuilder code) {
    try {
      if (node instanceof Num) {
        Method getTipo = Num.class.getMethod("getTipo");
        Object tipoEnum = getTipo.invoke(node);
        String tipoStr = tipoEnum.toString();

        code.append(tipoStr.equals("INTEGER") ? "IntToStr(" : "FloatToStr(");
        translateExpressionNode(node, code);
        code.append(")");
      } else if (node instanceof BooleanNode) {
        code.append("BoolToStr(");
        translateExpressionNode(node, code);
        code.append(", 'luz', 'trevas')");
      } else if (node instanceof VarNode) {
        Method getName = VarNode.class.getMethod("getName");
        String varName = normalize((String) getName.invoke(node));

        // Verificar tipo para escolher a conversão adequada
        String type = variableTypes.getOrDefault(varName, "Unknown");

        switch (type) {
          case "INTEGER":
            code.append("IntToStr(").append(varName).append(")");
            break;
          case "DECIMAL":
            code.append("FloatToStr(").append(varName).append(")");
            break;
          case "BOOLEAN":
            code.append("BoolToStr(").append(varName).append(", 'luz', 'trevas')");
            break;
          case "STRING":
            code.append(varName); // Já é string, não precisa converter
            break;
          default:
            code.append("VarToStr(").append(varName).append(")");
        }
      } else if (node instanceof CallExpr) {
        // Para chamadas de função, envolvemos em VarToStr para garantir
        code.append("VarToStr(");
        translateExpressionNode(node, code);
        code.append(")");
      } else {
        // Para outros casos, tentamos tradução direta
        translateExpressionNode(node, code);
      }
    } catch (Exception e) {
      code.append("{ Erro na conversão para string: ").append(e.getMessage()).append(" }");
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
            localVars.add(varName);
          }
        }
      }
    } catch (Exception e) {
      // Ignorar erros
    }
  }

  private static boolean hasReturnNode(BlockNode node) {
    try {
      Field stmtsField = BlockNode.class.getDeclaredField("statements");
      stmtsField.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

      for (ASTNode stmt : statements) {
        if (stmt instanceof ReturnNode) {
          return true;
        } else if (stmt instanceof BlockNode) {
          if (hasReturnNode((BlockNode) stmt)) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      // Ignorar erros
    }
    return false;
  }

  /**
   * Remove ":" do início dos identificadores PSALMS para adequar ao Pascal.
   */
  private static String normalize(String id) {
    return id.startsWith(":") ? id.substring(1) : id;
  }
}