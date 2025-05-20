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
 * Tradutor que converte AST da linguagem PSALMS para código Pascal executável.
 */
public class PascalTranslator {
    // Variável para rastrear o nome da função atual sendo processada
    private static String currentFunctionName = "";

    // Mapa para rastrear os tipos das variáveis
    private static Map<String, String> variableTypes = new HashMap<>();

    // Conjunto para rastrear variáveis já declaradas
    private static Set<String> declaredVariables = new HashSet<>();

    /**
     * Traduz a AST para código Pascal.
     */
    public static String translate(ASTNode ast) {
        StringBuilder code = new StringBuilder();
        StringBuilder functions = new StringBuilder();
        StringBuilder mainCode = new StringBuilder();
        Set<String> variables = new HashSet<>();
        variableTypes.clear(); // Limpa o mapa de tipos antes de iniciar
        declaredVariables.clear(); // Limpa o conjunto de variáveis declaradas
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
            case "FLOAT":
                return "Double";
            case "BOOLEAN":
                return "Boolean";
            case "STRING":
                return "String";
            default:
                return "String"; // Padrão para evitar problemas de compatibilidade
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
                    
                    // Adicionar parâmetros como variáveis
                    Field paramsField = FunctionDeclNode.class.getDeclaredField("params");
                    paramsField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<ParamNode> params = (List<ParamNode>) paramsField.get(node);
                    
                    for (ParamNode param : params) {
                        Method getName = ParamNode.class.getMethod("getName");
                        String paramName = normalize((String) getName.invoke(param));
                        variables.add(paramName);
                        variableTypes.put(paramName, "VARIANT"); // Parâmetros são variant por padrão
                    }
                }

                // Coletar variáveis locais dentro da função
                Field bodyField = FunctionDeclNode.class.getDeclaredField("body");
                bodyField.setAccessible(true);
                BlockNode body = (BlockNode) bodyField.get(node);
                collectDeclarations(body, variables, functions);
            } else if (node instanceof InputNode) {
                // Identificar variável de entrada
                Field varField = InputNode.class.getDeclaredField("variavel");
                varField.setAccessible(true);
                ASTNode var = (ASTNode) varField.get(node);
                
                if (var instanceof VarNode) {
                    Method getName = VarNode.class.getMethod("getName");
                    String varName = normalize((String) getName.invoke(var));
                    variables.add(varName);
                    declaredVariables.add(varName);
                    
                    // A princípio, considerar entrada como String
                    variableTypes.put(varName, "STRING");
                }
            } else if (node instanceof IfNode) {
                // Processar condição e blocos
                Field condField = IfNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                collectDeclarations(cond, variables, functions);
                
                Field thenField = IfNode.class.getDeclaredField("thenBlock");
                thenField.setAccessible(true);
                BlockNode thenBlock = (BlockNode) thenField.get(node);
                collectDeclarations(thenBlock, variables, functions);
                
                Field elseField = IfNode.class.getDeclaredField("elseBlock");
                elseField.setAccessible(true);
                BlockNode elseBlock = (BlockNode) elseField.get(node);
                if (elseBlock != null) {
                    collectDeclarations(elseBlock, variables, functions);
                }
                
                Field elseIfsField = IfNode.class.getDeclaredField("elseIfs");
                elseIfsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ElseIfNode> elseIfs = (List<ElseIfNode>) elseIfsField.get(node);
                for (ElseIfNode elseIf : elseIfs) {
                    collectDeclarations(elseIf, variables, functions);
                }
            } else if (node instanceof ElseIfNode) {
                Field condField = ElseIfNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                collectDeclarations(cond, variables, functions);
                
                Field corpoField = ElseIfNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                BlockNode corpo = (BlockNode) corpoField.get(node);
                collectDeclarations(corpo, variables, functions);
            } else if (node instanceof WhileLoopNode) {
                Field condField = WhileLoopNode.class.getDeclaredField("condicao");
                condField.setAccessible(true);
                ASTNode cond = (ASTNode) condField.get(node);
                collectDeclarations(cond, variables, functions);
                
                Field corpoField = WhileLoopNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                BlockNode corpo = (BlockNode) corpoField.get(node);
                collectDeclarations(corpo, variables, functions);
            } else if (node instanceof ForLoopNode) {
                Field inicField = ForLoopNode.class.getDeclaredField("inicializacao");
                inicField.setAccessible(true);
                ASTNode inic = (ASTNode) inicField.get(node);
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
            // Ignorar erros na coleta
            System.err.println("Erro na coleta de declarações: " + e.getMessage());
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

                Method getLeft = BinOpNode.class.getMethod("getLeft");
                Method getRight = BinOpNode.class.getMethod("getRight");
                ASTNode left = (ASTNode) getLeft.invoke(valueNode);
                ASTNode right = (ASTNode) getRight.invoke(valueNode);

                if ("+".equals(op)) {
                    // Se qualquer um dos lados for string, o resultado será string
                    if ((left instanceof Str) || (right instanceof Str)) {
                        variableTypes.put(varName, "STRING");
                    } else if (isStringExpression(left) || isStringExpression(right)) {
                        variableTypes.put(varName, "STRING");
                    } else {
                        // Para outros casos, assumimos decimal para operações numéricas
                        variableTypes.put(varName, "DECIMAL");
                    }
                } else if ("==".equals(op) || "!=".equals(op) || ">".equals(op) || "<".equals(op) || 
                          ">=".equals(op) || "<=".equals(op)) {
                    // Operadores relacionais resultam em booleano
                    variableTypes.put(varName, "BOOLEAN");
                } else {
                    // Outros operadores como -, *, / geralmente resultam em números
                    variableTypes.put(varName, "DECIMAL");
                }
            } else if (valueNode instanceof CallExpr) {
                // Para chamadas de função, poderíamos implementar uma análise de retorno
                // Por enquanto, assumimos Variant
                variableTypes.put(varName, "VARIANT");
            } else if (valueNode instanceof VarNode) {
                // Se estamos atribuindo uma variável à outra, herda o tipo
                Method getName = VarNode.class.getMethod("getName");
                String sourceVarName = normalize((String) getName.invoke(valueNode));
                String sourceType = variableTypes.get(sourceVarName);
                if (sourceType != null) {
                    variableTypes.put(varName, sourceType);
                } else {
                    variableTypes.put(varName, "VARIANT");
                }
            }
        } catch (Exception e) {
            // Em caso de erro, assumimos tipo desconhecido
            variableTypes.put(varName, "VARIANT");
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
                        // Função vai para a seção de funções
                        translateNode(stmt, functions, 0);
                    } else {
                        // Código normal vai para o corpo principal
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
                        // Função vai para a seção de funções
                        translateNode(stmt, functions, 0);
                    } else {
                        // Código normal vai para o corpo principal
                        translateNode(stmt, mainCode, 1);
                    }
                }
            } else {
                // Caso seja um nó que não é um bloco ou programa, traduzimos para o corpo principal
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
            } else if (node instanceof InputNode) {
                // Input ($ler(...)) -> ReadLn
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
                // For loop -> Pode ser traduzido como For ou While em Pascal
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

                // Analisar se podemos usar FOR em Pascal (contagem simples e crescente)
                boolean useForLoop = canUseForLoop(inic, cond, inc);
                
                if (useForLoop) {
                    // Extrair variável de controle e valores inicial/final
                    ForLoopInfo loopInfo = extractForLoopInfo(inic, cond, inc);
                    
                    if (loopInfo != null) {
                        code.append(indentation).append("FOR ").append(loopInfo.varName)
                            .append(" := ").append(loopInfo.initialValue)
                            .append(" TO ").append(loopInfo.finalValue)
                            .append(" DO\n").append(indentation).append("BEGIN\n");
                        
                        translateNode(body, code, indent + 1);
                        
                        code.append(indentation).append("END;\n");
                    } else {
                        // Fallback para WHILE se algo der errado
                        useForLoop = false;
                    }
                } 
                
                if (!useForLoop) {
                    // Usar WHILE em vez de FOR
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
                
                code.append(indentation).append("// ").append(texto.trim()).append("\n");
            } else if (node instanceof TryNode) {
                // Bloco Try simples
                Field tryBlockField = TryNode.class.getDeclaredField("tryBlock");
                tryBlockField.setAccessible(true);
                BlockNode tryBlock = (BlockNode) tryBlockField.get(node);
                
                code.append(indentation).append("TRY\n");
                code.append(indentation).append("BEGIN\n");
                translateNode(tryBlock, code, indent + 1);
                code.append(indentation).append("END;\n");
            } else if (node instanceof TryCatchNode) {
                // Try-Catch completo
                Field tryBlockField = TryCatchNode.class.getDeclaredField("tryBlock");
                tryBlockField.setAccessible(true);
                BlockNode tryBlock = (BlockNode) tryBlockField.get(node);
                
                Field exceptionVarField = TryCatchNode.class.getDeclaredField("exceptionVar");
                exceptionVarField.setAccessible(true);
                String exceptionVar = normalize((String) exceptionVarField.get(node));
                
                Field catchBlockField = TryCatchNode.class.getDeclaredField("catchBlock");
                catchBlockField.setAccessible(true);
                BlockNode catchBlock = (BlockNode) catchBlockField.get(node);
                
                code.append(indentation).append("TRY\n");
                code.append(indentation).append("BEGIN\n");
                translateNode(tryBlock, code, indent + 1);
                code.append(indentation).append("END\n");
                code.append(indentation).append("EXCEPT ON E: Exception DO\n");
                code.append(indentation).append("BEGIN\n");
                code.append(indentation).append("  ").append(exceptionVar).append(" := E.Message;\n");
                translateNode(catchBlock, code, indent + 1);
                code.append(indentation).append("END;\n");
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
                    translatePrintExpression(node, code);
                }
            } else {
                // Outro tipo de nó
                translatePrintExpression(node, code);
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
                    case "FLOAT":
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
            } else if (node instanceof CallExpr) {
                // Para chamadas de função, usamos VarToStr
                code.append("VarToStr(");
                translateExpressionNode(node, code);
                code.append(")");
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
                    else if (op.equals("&&"))
                        pascalOp = "AND";
                    else if (op.equals("||"))
                        pascalOp = "OR";

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
                    case "FLOAT":
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
                        if (!declaredVariables.contains(varName)) {
                            localVars.add(varName);
                            declaredVariables.add(varName);
                        }
                    }
                } else if (stmt instanceof BlockNode) {
                    // Recursivamente procura em blocos aninhados
                    collectLocalVars((BlockNode) stmt, localVars);
                } else if (stmt instanceof IfNode) {
                    // Procura em blocos if/then/else
                    try {
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
                        
                        Field elseIfsField = IfNode.class.getDeclaredField("elseIfs");
                        elseIfsField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        List<ElseIfNode> elseIfs = (List<ElseIfNode>) elseIfsField.get(stmt);
                        for (ElseIfNode elseIf : elseIfs) {
                            Field corpoField = ElseIfNode.class.getDeclaredField("corpo");
                            corpoField.setAccessible(true);
                            BlockNode corpo = (BlockNode) corpoField.get(elseIf);
                            collectLocalVars(corpo, localVars);
                        }
                    } catch (Exception e) {
                        // Ignorar erros
                    }
                } else if (stmt instanceof WhileLoopNode) {
                    // Procura em blocos de loop
                    try {
                        Field corpoField = WhileLoopNode.class.getDeclaredField("corpo");
                        corpoField.setAccessible(true);
                        BlockNode corpo = (BlockNode) corpoField.get(stmt);
                        collectLocalVars(corpo, localVars);
                    } catch (Exception e) {
                        // Ignorar erros
                    }
                } else if (stmt instanceof ForLoopNode) {
                    // Procura em blocos de loop for
                    try {
                        Field corpoField = ForLoopNode.class.getDeclaredField("corpo");
                        corpoField.setAccessible(true);
                        BlockNode corpo = (BlockNode) corpoField.get(stmt);
                        collectLocalVars(corpo, localVars);
                    } catch (Exception e) {
                        // Ignorar erros
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
                } else if (stmt instanceof IfNode) {
                    // Verificar blocos then/else
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
     * Verifica se um loop pode ser convertido para a estrutura FOR do Pascal
     */
    private static boolean canUseForLoop(ASTNode init, ASTNode cond, ASTNode inc) {
        try {
            // Verificar se a inicialização é uma atribuição simples
            if (!(init instanceof AssignNode)) {
                return false;
            }
            
            // Verificar se a condição é uma comparação simples (<, <=, >, >=)
            if (!(cond instanceof BinOpNode)) {
                return false;
            }
            
            Method getOpMethod = BinOpNode.class.getMethod("getOperator");
            String condOp = (String) getOpMethod.invoke(cond);
            
            if (!("<".equals(condOp) || "<=".equals(condOp) || ">".equals(condOp) || ">=".equals(condOp))) {
                return false;
            }
            
            // Verificar se o incremento é simples
            if (!(inc instanceof AssignNode)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extrai informações de um loop FOR para tradução 
     */
    private static ForLoopInfo extractForLoopInfo(ASTNode init, ASTNode cond, ASTNode inc) {
        try {
            // Extrair variável de controle da inicialização
            Field lhsField = AssignNode.class.getDeclaredField("lhs");
            lhsField.setAccessible(true);
            ASTNode lhs = (ASTNode) lhsField.get(init);
            
            if (!(lhs instanceof VarNode)) {
                return null;
            }
            
            Method getName = VarNode.class.getMethod("getName");
            String varName = normalize((String) getName.invoke(lhs));
            
            // Extrair valor inicial
            Field rhsField = AssignNode.class.getDeclaredField("rhs");
            rhsField.setAccessible(true);
            ASTNode initRhs = (ASTNode) rhsField.get(init);
            
            StringBuilder initialValue = new StringBuilder();
            translateExpressionNode(initRhs, initialValue);
            
            // Extrair condição final
            Method getLeftMethod = BinOpNode.class.getMethod("getLeft");
            Method getRightMethod = BinOpNode.class.getMethod("getRight");
            Method getOpMethod = BinOpNode.class.getMethod("getOperator");
            
            ASTNode condLeft = (ASTNode) getLeftMethod.invoke(cond);
            ASTNode condRight = (ASTNode) getRightMethod.invoke(cond);
            String condOp = (String) getOpMethod.invoke(cond);
            
            // Verificar se a variável de controle está no lado esquerdo da condição
            boolean isVarOnLeft = false;
            if (condLeft instanceof VarNode) {
                String condVarName = normalize((String) getName.invoke(condLeft));
                isVarOnLeft = varName.equals(condVarName);
            }
            
            // Extrair valor final
            StringBuilder finalValue = new StringBuilder();
            if (isVarOnLeft) {
                translateExpressionNode(condRight, finalValue);
                
                // Ajustar para o formato exclusivo/inclusivo do FOR em Pascal
                if ("<".equals(condOp)) {
                    // i < 10 -> i := 0 to 9
                    finalValue = new StringBuilder(finalValue.toString() + " - 1");
                } else if (">".equals(condOp)) {
                    // Invertendo para ordem decrescente
                    // i > 0 -> i := 10 downto 1
                    return null; // Não suportamos downto nesta implementação
                } else if (">=".equals(condOp)) {
                    // Invertendo para ordem decrescente
                    // i >= 0 -> i := 10 downto 0
                    return null; // Não suportamos downto nesta implementação
                }
                // <= não precisa de ajuste
            } else {
                // Condição no formato: 10 > i 
                translateExpressionNode(condLeft, finalValue);
                
                if (">".equals(condOp)) {
                    // 10 > i -> i := 0 to 9
                    finalValue = new StringBuilder(finalValue.toString() + " - 1");
                } else if ("<".equals(condOp)) {
                    // Não suportamos esta forma
                    return null;
                } else if ("<=".equals(condOp)) {
                    // Não suportamos esta forma
                    return null;
                }
                // >= não precisa de ajuste
            }
            
            // Verificar incremento/decremento
            ASTNode incLhs = (ASTNode) lhsField.get(inc);
            ASTNode incRhs = (ASTNode) rhsField.get(inc);
            
            if (!(incLhs instanceof VarNode)) {
                return null;
            }
            
            String incVarName = normalize((String) getName.invoke(incLhs));
            if (!varName.equals(incVarName)) {
                return null; // Variável de incremento diferente
            }
            
            // Verificar se o incremento é simples: i = i + 1
            if (!(incRhs instanceof BinOpNode)) {
                return null;
            }
            
            ASTNode incLeft = (ASTNode) getLeftMethod.invoke(incRhs);
            ASTNode incRight = (ASTNode) getRightMethod.invoke(incRhs);
            String incOp = (String) getOpMethod.invoke(incRhs);
            
            if (!"+".equals(incOp) || !(incLeft instanceof VarNode)) {
                return null;
            }
            
            String incLeftVarName = normalize((String) getName.invoke(incLeft));
            if (!varName.equals(incLeftVarName)) {
                return null;
            }
            
            // Verificar se o incremento é 1
            if (!(incRight instanceof Num)) {
                return null;
            }
            
            Method getValor = Num.class.getMethod("getValor");
            String valor = (String) getValor.invoke(incRight);
            
            if (!"1".equals(valor)) {
                return null; // FOR em Pascal só suporta incremento/decremento de 1
            }
            
            return new ForLoopInfo(varName, initialValue.toString(), finalValue.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Remove ":" do início dos identificadores PSALMS para adequar ao Pascal.
     */
    private static String normalize(String id) {
        return id.startsWith(":") ? id.substring(1) : id;
    }
    
    /**
     * Classe auxiliar para informações de loop FOR
     */
    private static class ForLoopInfo {
        public final String varName;
        public final String initialValue;
        public final String finalValue;
        
        public ForLoopInfo(String varName, String initialValue, String finalValue) {
            this.varName = varName;
            this.initialValue = initialValue;
            this.finalValue = finalValue;
        }
    }
}