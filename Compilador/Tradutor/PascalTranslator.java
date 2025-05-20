package Compilador.Tradutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
 * Tradutor melhorado que converte AST da linguagem PSALMS para código Pascal.
 * Realiza inferência de tipos correta e evita duplicação de código.
 */
public class PascalTranslator {
    // Variável para rastrear o nome da função atual sendo processada
    private static String currentFunctionName = "";

    // Mapa para rastrear os tipos das variáveis
    private static Map<String, String> variableTypes = new HashMap<>();

    // Conjunto para rastrear variáveis já declaradas
    private static Set<String> declaredVariables = new HashSet<>();

    // Conjunto para rastrear variáveis que são usadas como contadores em loops
    private static Set<String> counterVariables = new HashSet<>();

    // Set para rastrear funções e procedimentos declarados
    private static Set<String> declaredFunctions = new HashSet<>();

    // Set para rastrear funções e procedimentos chamados
    private static Set<String> calledFunctions = new HashSet<>();

    /**
     * Traduz a AST para código Pascal.
     */
    public static String translate(ASTNode ast) {
        StringBuilder code = new StringBuilder();
        StringBuilder functions = new StringBuilder();
        StringBuilder mainCode = new StringBuilder();
        Set<String> variables = new HashSet<>();
        variableTypes.clear();
        declaredVariables.clear();
        counterVariables.clear();
        declaredFunctions.clear();
        calledFunctions.clear();
        currentFunctionName = "";

        // Primeira passagem: identificar variáveis e funções
        collectDeclarations(ast, variables, declaredFunctions);

        // Segunda passagem: rastrear funções chamadas
        trackFunctionCalls(ast);

        // Processar ast para separar funções do código principal
        processAST(ast, functions, mainCode);

        // Cabeçalho do programa Pascal
        code.append("PROGRAM PsalmsProgram;\n\n");

        // Seção de variáveis
        if (!variables.isEmpty()) {
            code.append("VAR\n");
            for (String variable : variables) {
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

        // Comentar procedimentos/funções definidos mas não chamados
        for (String funcName : declaredFunctions) {
            if (!calledFunctions.contains(funcName) && !funcName.equals("main")) {
                code.append("  (* ATENÇÃO: ").append(hasReturnValue(funcName) ? "Função" : "Procedimento");
                code.append(" '").append(funcName).append("' foi definido mas não utilizado *)\n");
            }
        }
        code.append("END.\n");

        return code.toString();
    }

    /**
     * Verifica se uma função tem valor de retorno
     */
    private static boolean hasReturnValue(String functionName) {
        // Implementação simples: verificar o tipo inferido
        return variableTypes.containsKey(functionName);
    }

    /**
     * Determina o tipo Pascal apropriado baseado no uso
     */
    private static String getPascalType(String variable) {
        if (counterVariables.contains(variable)) {
            return "Integer";
        }

        String type = variableTypes.getOrDefault(variable, "Integer"); // Padrão: Integer

        switch (type) {
            case "INTEGER":
                return "Integer";
            case "DECIMAL":
            case "FLOAT":
                return "Real";
            case "BOOLEAN":
                return "Boolean";
            case "STRING":
                return "String";
            default:
                return "Integer"; // Tipo padrão
        }
    }

    /**
     * Coleta declarações de variáveis e funções.
     */
    private static void collectDeclarations(ASTNode node, Set<String> variables, Set<String> functions) {
        if (node == null)
            return;

        try {
            if (node instanceof ProgramNode) {
                Field stmtsField = ProgramNode.class.getDeclaredField("statements");
                stmtsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

                for (ASTNode stmt : statements) {
                    collectDeclarations(stmt, variables, functions);
                }
            } else if (node instanceof BlockNode) {
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
                    declaredVariables.add(varName);
                    inferType(varName, rhs);

                    // Verifica se é incremento/decremento para detectar contador
                    if (rhs instanceof BinOpNode) {
                        Method getOpMethod = BinOpNode.class.getMethod("getOperator");
                        Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
                        String op = (String) getOpMethod.invoke(rhs);
                        ASTNode leftNode = (ASTNode) getLeftMethod.invoke(rhs);

                        if (("+".equals(op) || "-".equals(op)) && leftNode instanceof VarNode) {
                            Method getLeftName = VarNode.class.getMethod("getName");
                            String leftName = normalize((String) getLeftName.invoke(leftNode));

                            if (varName.equals(leftName)) {
                                // É um incremento ou decremento de si mesmo
                                counterVariables.add(varName);
                            }
                        }
                    }
                }

                collectDeclarations(rhs, variables, functions);
            } else if (node instanceof FunctionDeclNode) {
                if (functions != null) {
                    Field nameField = FunctionDeclNode.class.getDeclaredField("name");
                    nameField.setAccessible(true);
                    String name = normalize((String) nameField.get(node));
                    functions.add(name);

                    Field paramsField = FunctionDeclNode.class.getDeclaredField("params");
                    paramsField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<ParamNode> params = (List<ParamNode>) paramsField.get(node);

                    for (ParamNode param : params) {
                        Method getName = ParamNode.class.getMethod("getName");
                        String paramName = normalize((String) getName.invoke(param));
                        variables.add(paramName);
                        variableTypes.put(paramName, "INTEGER"); // Parâmetros são INTEGER por padrão
                    }
                }

                Field bodyField = FunctionDeclNode.class.getDeclaredField("body");
                bodyField.setAccessible(true);
                BlockNode body = (BlockNode) bodyField.get(node);
                collectDeclarations(body, variables, functions);
            } else if (node instanceof InputNode) {
                Field varField = InputNode.class.getDeclaredField("variavel");
                varField.setAccessible(true);
                ASTNode var = (ASTNode) varField.get(node);

                if (var instanceof VarNode) {
                    Method getName = VarNode.class.getMethod("getName");
                    String varName = normalize((String) getName.invoke(var));
                    variables.add(varName);
                    declaredVariables.add(varName);
                    variableTypes.put(varName, "INTEGER"); // Entrada implicitamente INTEGER por padrão
                }
            } else if (node instanceof WhileLoopNode) {
                Field condField = WhileLoopNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);

                // Verificar se usa contador
                if (cond instanceof BinOpNode) {
                    Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
                    ASTNode leftNode = (ASTNode) getLeftMethod.invoke(cond);

                    if (leftNode instanceof VarNode) {
                        Method getName = VarNode.class.getMethod("getName");
                        String varName = normalize((String) getName.invoke(leftNode));
                        counterVariables.add(varName); // Variável usada em condição de loop = contador
                    }
                }

                collectDeclarations(cond, variables, functions);

                Field corpoField = WhileLoopNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                BlockNode corpo = (BlockNode) corpoField.get(node);
                collectDeclarations(corpo, variables, functions);
            } else if (node instanceof ForLoopNode) {
                Field inicField = ForLoopNode.class.getDeclaredField("inicializacao");
                inicField.setAccessible(true);
                ASTNode inic = (ASTNode) inicField.get(node);

                // Extrair variável de controle do loop como contador
                if (inic instanceof AssignNode) {
                    Field lhsField = AssignNode.class.getDeclaredField("lhs");
                    lhsField.setAccessible(true);
                    ASTNode lhs = (ASTNode) lhsField.get(inic);

                    if (lhs instanceof VarNode) {
                        Method getName = VarNode.class.getMethod("getName");
                        String varName = normalize((String) getName.invoke(lhs));
                        counterVariables.add(varName); // Variável de controle do loop = contador
                    }
                }

                collectDeclarations(inic, variables, functions);

                Field condField = ForLoopNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                collectDeclarations(cond, variables, functions);

                Field incField = ForLoopNode.class.getDeclaredField("incremento");
                incField.setAccessible(true);
                ASTNode inc = (ASTNode) incField.get(node);
                collectDeclarations(inc, variables, functions);

                Field corpoField = ForLoopNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                BlockNode corpo = (BlockNode) corpoField.get(node);
                collectDeclarations(corpo, variables, functions);
            }
        } catch (Exception e) {
            System.err.println("Erro na coleta de declarações: " + e.getMessage());
        }
    }

    /**
     * Rastrear chamadas de funções para saber quais foram usadas
     */
    private static void trackFunctionCalls(ASTNode node) {
        if (node == null)
            return;

        try {
            if (node instanceof ProgramNode || node instanceof BlockNode) {
                Field stmtsField = node instanceof ProgramNode ? ProgramNode.class.getDeclaredField("statements")
                        : BlockNode.class.getDeclaredField("statements");
                stmtsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

                for (ASTNode stmt : statements) {
                    trackFunctionCalls(stmt);
                }
            } else if (node instanceof CallNode || node instanceof CallExpr) {
                // Chamada de função
                String funcName;

                if (node instanceof CallNode) {
                    Method getFuncName = CallNode.class.getMethod("getFunctionName");
                    funcName = normalize((String) getFuncName.invoke(node));
                } else { // CallExpr
                    Field nameField = CallExpr.class.getDeclaredField("functionName");
                    nameField.setAccessible(true);
                    funcName = normalize((String) nameField.get(node));
                }

                calledFunctions.add(funcName);

                // Processa argumentos recursivamente
                if (node instanceof CallNode) {
                    Method getArgs = CallNode.class.getMethod("getArguments");
                    @SuppressWarnings("unchecked")
                    List<ASTNode> args = (List<ASTNode>) getArgs.invoke(node);
                    for (ASTNode arg : args) {
                        trackFunctionCalls(arg);
                    }
                } else { // CallExpr
                    Field argsField = CallExpr.class.getDeclaredField("args");
                    argsField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<ASTNode> args = (List<ASTNode>) argsField.get(node);
                    for (ASTNode arg : args) {
                        trackFunctionCalls(arg);
                    }
                }
            } else if (node instanceof AssignNode) {
                Field rhsField = AssignNode.class.getDeclaredField("rhs");
                rhsField.setAccessible(true);
                ASTNode rhs = (ASTNode) rhsField.get(node);
                trackFunctionCalls(rhs);
            } else if (node instanceof IfNode) {
                // Rastrear nas condições e blocos
                Field condField = IfNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                trackFunctionCalls(cond);

                Field thenField = IfNode.class.getDeclaredField("thenBlock");
                thenField.setAccessible(true);
                BlockNode thenBlock = (BlockNode) thenField.get(node);
                trackFunctionCalls(thenBlock);

                Field elseField = IfNode.class.getDeclaredField("elseBlock");
                elseField.setAccessible(true);
                BlockNode elseBlock = (BlockNode) elseField.get(node);
                if (elseBlock != null) {
                    trackFunctionCalls(elseBlock);
                }
            } else if (node instanceof WhileLoopNode) {
                Field condField = WhileLoopNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                trackFunctionCalls(cond);

                Field corpoField = WhileLoopNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                BlockNode corpo = (BlockNode) corpoField.get(node);
                trackFunctionCalls(corpo);
            } else if (node instanceof ForLoopNode) {
                Field inicField = ForLoopNode.class.getDeclaredField("inicializacao");
                inicField.setAccessible(true);
                ASTNode inic = (ASTNode) inicField.get(node);
                trackFunctionCalls(inic);

                Field condField = ForLoopNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                trackFunctionCalls(cond);

                Field incField = ForLoopNode.class.getDeclaredField("incremento");
                incField.setAccessible(true);
                ASTNode inc = (ASTNode) incField.get(node);
                trackFunctionCalls(inc);

                Field corpoField = ForLoopNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                BlockNode corpo = (BlockNode) corpoField.get(node);
                trackFunctionCalls(corpo);
            }
        } catch (Exception e) {
            System.err.println("Erro ao rastrear chamadas de função: " + e.getMessage());
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

                // Se é um número pequeno (0, 1, etc.), provavelmente é um contador
                Method getValor = Num.class.getMethod("getValor");
                String valor = (String) getValor.invoke(valueNode);
                int valorNum = Integer.parseInt(valor);
                if (valorNum >= 0 && valorNum <= 10) {
                    // Provavelmente é um contador
                    counterVariables.add(varName);
                }
            } else if (valueNode instanceof BooleanNode) {
                variableTypes.put(varName, "BOOLEAN");
            } else if (valueNode instanceof Str) {
                variableTypes.put(varName, "STRING");
            } else if (valueNode instanceof BinOpNode) {
                Method getOpMethod = BinOpNode.class.getMethod("getOperator");
                String op = (String) getOpMethod.invoke(valueNode);

                Method getLeft = BinOpNode.class.getMethod("getLeft");
                Method getRight = BinOpNode.class.getMethod("getRight");
                ASTNode left = (ASTNode) getLeft.invoke(valueNode);
                ASTNode right = (ASTNode) getRight.invoke(valueNode);

                if ("+".equals(op)) {
                    if ((left instanceof Str) || (right instanceof Str)) {
                        variableTypes.put(varName, "STRING");
                    } else if (isStringExpression(left) || isStringExpression(right)) {
                        variableTypes.put(varName, "STRING");
                    } else {
                        variableTypes.put(varName, "INTEGER");
                    }
                } else if ("==".equals(op) || "!=".equals(op) || ">".equals(op) || "<".equals(op) ||
                        ">=".equals(op) || "<=".equals(op)) {
                    variableTypes.put(varName, "BOOLEAN");
                } else {
                    variableTypes.put(varName, "INTEGER");
                }
            } else if (valueNode instanceof CallExpr) {
                // Tipo baseado na função chamada
                Field nameField = CallExpr.class.getDeclaredField("functionName");
                nameField.setAccessible(true);
                String funcName = normalize((String) nameField.get(valueNode));

                // Assume mesmo tipo da função se conhecida
                if (variableTypes.containsKey(funcName)) {
                    variableTypes.put(varName, variableTypes.get(funcName));
                } else {
                    variableTypes.put(varName, "INTEGER"); // Padrão
                }
            } else if (valueNode instanceof VarNode) {
                Method getName = VarNode.class.getMethod("getName");
                String sourceVarName = normalize((String) getName.invoke(valueNode));

                // Herdar tipo
                if (variableTypes.containsKey(sourceVarName)) {
                    variableTypes.put(varName, variableTypes.get(sourceVarName));

                    // Herdar status de contador também
                    if (counterVariables.contains(sourceVarName)) {
                        counterVariables.add(varName);
                    }
                } else {
                    variableTypes.put(varName, "INTEGER"); // Padrão
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao inferir tipo: " + e.getMessage());
            variableTypes.put(varName, "INTEGER"); // Padrão seguro
        }
    }

    /**
     * Processa a AST, separando funções do código principal.
     */
    private static void processAST(ASTNode node, StringBuilder functions, StringBuilder mainCode) {
        if (node == null)
            return;

        try {
            if (node instanceof ProgramNode) {
                Field stmtsField = ProgramNode.class.getDeclaredField("statements");
                stmtsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

                for (ASTNode stmt : statements) {
                    if (stmt instanceof FunctionDeclNode) {
                        translateNode(stmt, functions, 0);
                    } else {
                        translateNode(stmt, mainCode, 1);
                    }
                }
            } else if (node instanceof BlockNode) {
                Field stmtsField = BlockNode.class.getDeclaredField("statements");
                stmtsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

                for (ASTNode stmt : statements) {
                    if (stmt instanceof FunctionDeclNode) {
                        translateNode(stmt, functions, 0);
                    } else {
                        translateNode(stmt, mainCode, 1);
                    }
                }
            } else {
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
            if (node instanceof ProgramNode) {
                // Programa completo
                Field stmtsField = ProgramNode.class.getDeclaredField("statements");
                stmtsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

                for (ASTNode stmt : statements) {
                    translateNode(stmt, code, indent);
                }
            } else if (node instanceof BlockNode) {
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
                    boolean isCounter = counterVariables.contains(paramName);
                    code.append(paramName).append(": ").append(isCounter ? "Integer" : "Integer");
                    if (i < params.size() - 1)
                        code.append("; ");
                }
                code.append(")");

                if (hasReturn)
                    code.append(": ").append(getPascalType(name));
                code.append(";\n");

                // Variáveis locais
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

                // Processar corpo, evitando duplicações
                Set<String> processedAssignments = new HashSet<>();
                translateNodeWithoutDuplication(body, code, 1, processedAssignments);

                code.append("END;\n\n");
            } else if (node instanceof AssignNode) {
                // Atribuição
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
                    translateExpressionNode(lhs, code);
                }

                code.append(" := ");
                translateExpressionNode(rhs, code);
                code.append(";\n");
            } else if (node instanceof ReturnNode) {
                // Return (amen)
                Field exprField = ReturnNode.class.getDeclaredField("expression");
                exprField.setAccessible(true);
                ASTNode expr = (ASTNode) exprField.get(node);

                code.append(indentation).append(currentFunctionName).append(" := ");
                translateExpressionNode(expr, code);
                code.append(";\n");
            } else if (node instanceof PrintNode) {
                // Print
                Field contentField = PrintNode.class.getDeclaredField("conteudo");
                contentField.setAccessible(true);
                ASTNode content = (ASTNode) contentField.get(node);

                code.append(indentation).append("WriteLn(");
                translateExpressionNode(content, code);
                code.append(");\n");
            } else if (node instanceof InputNode) {
                // Input
                Field varField = InputNode.class.getDeclaredField("variavel");
                varField.setAccessible(true);
                ASTNode varNode = (ASTNode) varField.get(node);

                if (varNode instanceof VarNode) {
                    Method getName = VarNode.class.getMethod("getName");
                    String varName = normalize((String) getName.invoke(varNode));

                    code.append(indentation).append("ReadLn(").append(varName).append(");\n");
                }
            } else if (node instanceof IfNode) {
                // If-Then-Else
                Field condField = IfNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);

                Field thenField = IfNode.class.getDeclaredField("thenBlock");
                thenField.setAccessible(true);
                BlockNode thenBlock = (BlockNode) thenField.get(node);

                Field elseIfsField = IfNode.class.getDeclaredField("elseIfs");
                elseIfsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ElseIfNode> elseIfs = (List<ElseIfNode>) elseIfsField.get(node);

                Field elseField = IfNode.class.getDeclaredField("elseBlock");
                elseField.setAccessible(true);
                BlockNode elseBlock = (BlockNode) elseField.get(node);

                code.append(indentation).append("IF ");
                translateExpressionNode(cond, code);
                code.append(" THEN\n").append(indentation).append("BEGIN\n");

                translateNode(thenBlock, code, indent + 1);

                code.append(indentation).append("END");

                // Processar ElseIfs
                for (ElseIfNode elseIf : elseIfs) {
                    code.append("\n").append(indentation).append("ELSE IF ");

                    Field elseIfCondField = ElseIfNode.class.getDeclaredField("condicao");
                    elseIfCondField.setAccessible(true);
                    ASTNode elseIfCond = (ASTNode) elseIfCondField.get(elseIf);

                    translateExpressionNode(elseIfCond, code);

                    code.append(" THEN\n").append(indentation).append("BEGIN\n");

                    Field elseIfBodyField = ElseIfNode.class.getDeclaredField("corpo");
                    elseIfBodyField.setAccessible(true);
                    BlockNode elseIfBody = (BlockNode) elseIfBodyField.get(elseIf);

                    translateNode(elseIfBody, code, indent + 1);

                    code.append(indentation).append("END");
                }

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
                // For loop
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

                String varName = "";
                String startValue = "";
                String endValue = "";
                boolean isSimpleLoop = false;

                // Tentar extrair variável de controle
                if (inic instanceof AssignNode) {
                    Field lhsField = AssignNode.class.getDeclaredField("lhs");
                    lhsField.setAccessible(true);
                    ASTNode lhs = (ASTNode) lhsField.get(inic);

                    Field rhsField = AssignNode.class.getDeclaredField("rhs");
                    rhsField.setAccessible(true);
                    ASTNode rhs = (ASTNode) rhsField.get(inic);

                    if (lhs instanceof VarNode) {
                        Method getName = VarNode.class.getMethod("getName");
                        varName = normalize((String) getName.invoke(lhs));

                        if (rhs instanceof Num) {
                            Method getValor = Num.class.getMethod("getValor");
                            startValue = (String) getValor.invoke(rhs);

                            // Tenta extrair valor final da condição
                            if (cond instanceof BinOpNode) {
                                Method getOpMethod = BinOpNode.class.getMethod("getOperator");
                                Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
                                Method getRightMethod = BinOpNode.class.getMethod("getRight");

                                String op = (String) getOpMethod.invoke(cond);
                                ASTNode left = (ASTNode) getLeftMethod.invoke(cond);
                                ASTNode right = (ASTNode) getRightMethod.invoke(cond);

                                if (left instanceof VarNode && right instanceof Num) {
                                    String condVarName = normalize((String) getName.invoke(left));
                                    if (varName.equals(condVarName) && "<=".equals(op)) {
                                        Method getRightValor = Num.class.getMethod("getValor");
                                        endValue = (String) getRightValor.invoke(right);
                                        isSimpleLoop = true;
                                    }
                                }
                            }
                        }
                    }
                }

                if (isSimpleLoop) {
                    // Usar FOR em Pascal
                    code.append(indentation).append("FOR ").append(varName)
                            .append(" := ").append(startValue)
                            .append(" TO ").append(endValue)
                            .append(" DO\n").append(indentation).append("BEGIN\n");

                    translateNode(body, code, indent + 1);

                    code.append(indentation).append("END;\n");
                } else {
                    // Usar WHILE
                    translateNode(inic, code, indent);
                    code.append(indentation).append("WHILE ");
                    translateExpressionNode(cond, code);
                    code.append(" DO\n").append(indentation).append("BEGIN\n");
                    translateNode(body, code, indent + 1);
                    translateNode(inc, code, indent + 1);
                    code.append(indentation).append("END;\n");
                }
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
                for (int i = 0; i < casos.size(); i++) {
                    CaseNode caso = casos.get(i);
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
            } else if (node instanceof CallExpr || node instanceof CallNode) {
                // Chamada de função como expressão ou comando
                String funcName = "";
                List<ASTNode> args = new ArrayList<>();

                if (node instanceof CallExpr) {
                    Field nameField = CallExpr.class.getDeclaredField("functionName");
                    nameField.setAccessible(true);
                    funcName = normalize((String) nameField.get(node));

                    Field argsField = CallExpr.class.getDeclaredField("args");
                    argsField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<ASTNode> callArgs = (List<ASTNode>) argsField.get(node);
                    args = callArgs;
                } else {
                    Method getFuncName = CallNode.class.getMethod("getFunctionName");
                    funcName = normalize((String) getFuncName.invoke(node));

                    Method getArgs = CallNode.class.getMethod("getArguments");
                    @SuppressWarnings("unchecked")
                    List<ASTNode> callArgs = (List<ASTNode>) getArgs.invoke(node);
                    args = callArgs;
                }

                // Registrar que a função foi chamada
                calledFunctions.add(funcName);

                code.append(funcName).append("(");

                for (int i = 0; i < args.size(); i++) {
                    translateExpressionNode(args.get(i), code);
                    if (i < args.size() - 1)
                        code.append(", ");
                }

                code.append(")");

                // Se for um nó de comando (não expressão), adiciona ponto e vírgula
                if (node instanceof CallNode) {
                    code.append(";\n");
                }
            } else if (node instanceof CommentNode) {
                // Comentário
                Field textoField = CommentNode.class.getDeclaredField("texto");
                textoField.setAccessible(true);
                String texto = (String) textoField.get(node);

                code.append(indentation).append("(* ").append(texto.trim()).append(" *)\n");
            }
        } catch (Exception e) {
            code.append(indentation).append("{ Erro: ").append(e.getMessage()).append(" }\n");
        }
    }

    /**
     * Versão da função translateNode que evita duplicações de código
     */
    private static void translateNodeWithoutDuplication(ASTNode node, StringBuilder code, int indent,
            Set<String> processedAssignments) {
        if (node == null)
            return;

        String indentation = "  ".repeat(indent);

        try {
            if (node instanceof BlockNode) {
                Field stmtsField = BlockNode.class.getDeclaredField("statements");
                stmtsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

                for (ASTNode stmt : statements) {
                    if (stmt instanceof AssignNode) {
                        // Evitar duplicações de atribuições
                        Field lhsField = AssignNode.class.getDeclaredField("lhs");
                        lhsField.setAccessible(true);
                        ASTNode lhs = (ASTNode) lhsField.get(stmt);

                        if (lhs instanceof VarNode) {
                            Method getName = VarNode.class.getMethod("getName");
                            String varName = normalize((String) getName.invoke(lhs));

                            Field rhsField = AssignNode.class.getDeclaredField("rhs");
                            rhsField.setAccessible(true);
                            ASTNode rhs = (ASTNode) rhsField.get(stmt);

                            // Verificar se é uma atribuição simples para um valor constante
                            String assignKey = null;
                            if (rhs instanceof Num) {
                                Method getValor = Num.class.getMethod("getValor");
                                String valor = (String) getValor.invoke(rhs);
                                assignKey = varName + "=" + valor;
                            }

                            if (assignKey != null) {
                                // Se já processamos esta atribuição exata antes, pular
                                if (processedAssignments.contains(assignKey)) {
                                    continue;
                                }
                                processedAssignments.add(assignKey);
                            }
                        }
                    }

                    // Processar nó normalmente
                    translateNode(stmt, code, indent);
                }
            } else {
                // Para outros tipos de nós, traduzir normalmente
                translateNode(node, code, indent);
            }
        } catch (Exception e) {
            code.append(indentation).append("{ Erro: ").append(e.getMessage()).append(" }\n");
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
     * Métodos auxiliares para manipular expressões
     */
    private static void translateExpressionNode(ASTNode node, StringBuilder code) {
        try {
            if (node instanceof VarNode) {
                Method getName = VarNode.class.getMethod("getName");
                String varName = normalize((String) getName.invoke(node));
                code.append(varName);
            } else if (node instanceof Num) {
                Method getValor = Num.class.getMethod("getValor");
                String valor = (String) getValor.invoke(node);
                code.append(valor);
            } else if (node instanceof Str) {
                Method getValor = Str.class.getMethod("getValor");
                String valor = (String) getValor.invoke(node);
                code.append("'").append(valor.replace("'", "''")).append("'");
            } else if (node instanceof BooleanNode) {
                Method getLexema = BooleanNode.class.getMethod("getLexema");
                String lexema = (String) getLexema.invoke(node);
                if ("luz".equals(lexema)) {
                    code.append("True");
                } else {
                    code.append("False");
                }
            } else if (node instanceof BinOpNode) {
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
                else if (op.equals("&&"))
                    pascalOp = "AND";
                else if (op.equals("||"))
                    pascalOp = "OR";

                code.append("(");
                translateExpressionNode(left, code);
                code.append(" ").append(pascalOp).append(" ");
                translateExpressionNode(right, code);
                code.append(")");
            } else if (node instanceof CallExpr) {
                Field nameField = CallExpr.class.getDeclaredField("functionName");
                nameField.setAccessible(true);
                String funcName = normalize((String) nameField.get(node));

                Field argsField = CallExpr.class.getDeclaredField("args");
                argsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> args = (List<ASTNode>) argsField.get(node);

                // Registrar que a função foi chamada
                calledFunctions.add(funcName);

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
                        if (!declaredVariables.contains(varName)) {
                            localVars.add(varName);
                            declaredVariables.add(varName);
                        }
                    }
                } else if (stmt instanceof BlockNode) {
                    collectLocalVars((BlockNode) stmt, localVars);
                } else if (stmt instanceof IfNode) {
                    Field thenField = IfNode.class.getDeclaredField("thenBlock");
                    thenField.setAccessible(true);
                    BlockNode thenBlock = (BlockNode) thenField.get(stmt);
                    collectLocalVars(thenBlock, localVars);

                    Field elseField = IfNode.class.getDeclaredField("elseBlock");
                    elseField.setAccessible(true);
                    BlockNode elseBlock = (BlockNode) elseField.get(stmt);
                    if (elseBlock != null) {
                        collectLocalVars(elseBlock, localVars);
                    }
                } else if (stmt instanceof WhileLoopNode) {
                    Field corpoField = WhileLoopNode.class.getDeclaredField("corpo");
                    corpoField.setAccessible(true);
                    BlockNode corpo = (BlockNode) corpoField.get(stmt);
                    collectLocalVars(corpo, localVars);
                } else if (stmt instanceof ForLoopNode) {
                    Field corpoField = ForLoopNode.class.getDeclaredField("corpo");
                    corpoField.setAccessible(true);
                    BlockNode corpo = (BlockNode) corpoField.get(stmt);
                    collectLocalVars(corpo, localVars);
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
                } else if (stmt instanceof IfNode) {
                    Field thenField = IfNode.class.getDeclaredField("thenBlock");
                    thenField.setAccessible(true);
                    BlockNode thenBlock = (BlockNode) thenField.get(stmt);

                    if (hasReturnNode(thenBlock)) {
                        return true;
                    }

                    Field elseField = IfNode.class.getDeclaredField("elseBlock");
                    elseField.setAccessible(true);
                    BlockNode elseBlock = (BlockNode) elseField.get(stmt);

                    if (elseBlock != null && hasReturnNode(elseBlock)) {
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