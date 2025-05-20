package Compilador.Semantico;

import java.util.*;
import Compilador.ast.*;
import Compilador.ast.Comandos.*;
import Compilador.ast.Expressoes.*;
import Compilador.ast.Expressoes.Variaveis.*;

/**
 * Analisador Semântico para a linguagem PSALMS.
 * Realiza verificações semânticas durante a análise da AST.
 */
public class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private List<String> errors;
    private String currentFunction;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.currentFunction = null;
    }

    /**
     * Analisa a AST em busca de erros semânticos.
     * @param ast Raiz da árvore de sintaxe abstrata.
     * @return Lista de erros semânticos encontrados.
     */
    public List<String> analyze(ASTNode ast) {
        errors.clear();
        symbolTable.clear();
        visitNode(ast);
        return errors;
    }

    /**
     * Visita recursivamente os nós da AST.
     * @param node Nó atual.
     */
    private void visitNode(ASTNode node) {
        if (node == null) return;

        try {
            if (node instanceof BlockNode) {
                visitBlockNode((BlockNode) node);
            } else if (node instanceof ProgramNode) {
                visitProgramNode((ProgramNode) node);
            } else if (node instanceof AssignNode) {
                visitAssignNode((AssignNode) node);
            } else if (node instanceof FunctionDeclNode) {
                visitFunctionDeclNode((FunctionDeclNode) node);
            } else if (node instanceof CallExpr || node instanceof CallNode) {
                visitCallNode(node);
            } else if (node instanceof VarNode) {
                visitVarNode((VarNode) node);
            } else if (node instanceof ReturnNode) {
                visitReturnNode((ReturnNode) node);
            } else if (node instanceof IfNode) {
                visitIfNode((IfNode) node);
            } else if (node instanceof WhileLoopNode) {
                visitWhileLoopNode((WhileLoopNode) node);
            } else if (node instanceof ForLoopNode) {
                visitForLoopNode((ForLoopNode) node);
            } else if (node instanceof SwitchNode) {
                visitSwitchNode((SwitchNode) node);
            } else if (node instanceof BinOpNode) {
                visitBinOpNode((BinOpNode) node);
            } else if (node instanceof PrintNode) {
                visitPrintNode((PrintNode) node);
            } else if (node instanceof InputNode) {
                visitInputNode((InputNode) node);
            } else if (node instanceof TryCatchNode) {
                visitTryCatchNode((TryCatchNode) node);
            }
        } catch (Exception e) {
            errors.add("Erro interno no analisador semântico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void visitBlockNode(BlockNode node) {
        try {
            java.lang.reflect.Field stmtsField = BlockNode.class.getDeclaredField("statements");
            stmtsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

            // Visita cada comando no bloco
            for (ASTNode stmt : statements) {
                visitNode(stmt);
            }
        } catch (Exception e) {
            errors.add("Erro ao analisar bloco: " + e.getMessage());
        }
    }

    private void visitProgramNode(ProgramNode node) {
        try {
            java.lang.reflect.Field stmtsField = ProgramNode.class.getDeclaredField("statements");
            stmtsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ASTNode> statements = (List<ASTNode>) stmtsField.get(node);

            // Visita cada comando no programa
            for (ASTNode stmt : statements) {
                visitNode(stmt);
            }
        } catch (Exception e) {
            errors.add("Erro ao analisar programa: " + e.getMessage());
        }
    }

    private void visitAssignNode(AssignNode node) {
        try {
            java.lang.reflect.Field lhsField = AssignNode.class.getDeclaredField("lhs");
            lhsField.setAccessible(true);
            ASTNode lhs = (ASTNode) lhsField.get(node);

            java.lang.reflect.Field rhsField = AssignNode.class.getDeclaredField("rhs");
            rhsField.setAccessible(true);
            ASTNode rhs = (ASTNode) rhsField.get(node);

            // Processa o lado direito primeiro para verificar validade
            visitNode(rhs);
            String rhsType = getExpressionType(rhs);

            if (lhs instanceof VarNode) {
                VarNode varNode = (VarNode) lhs;
                String varName = varNode.getName();

                // Verifica se é uma variável existente ou uma declaração
                if (!symbolTable.exists(varName)) {
                    // Nova variável - adiciona à tabela de símbolos com tipo inferido
                    symbolTable.add(varName, rhsType, currentFunction);
                } else {
                    // Variável existente - verifica compatibilidade de tipos
                    String lhsType = symbolTable.getType(varName);
                    if (!isTypeCompatible(lhsType, rhsType)) {
                        errors.add("Erro: Incompatibilidade de tipos na atribuição. Variável '" + 
                                  varName + "' é do tipo " + lhsType + ", mas recebeu valor do tipo " + rhsType);
                    }
                }
            } else {
                errors.add("Erro: Lado esquerdo da atribuição não é uma variável válida");
            }
        } catch (Exception e) {
            errors.add("Erro ao analisar atribuição: " + e.getMessage());
        }
    }

    private void visitFunctionDeclNode(FunctionDeclNode node) {
        try {
            java.lang.reflect.Field nameField = FunctionDeclNode.class.getDeclaredField("name");
            nameField.setAccessible(true);
            String name = (String) nameField.get(node);

            java.lang.reflect.Field paramsField = FunctionDeclNode.class.getDeclaredField("params");
            paramsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ParamNode> params = (List<ParamNode>) paramsField.get(node);

            java.lang.reflect.Field bodyField = FunctionDeclNode.class.getDeclaredField("body");
            bodyField.setAccessible(true);
            BlockNode body = (BlockNode) bodyField.get(node);

            // Verifica se a função já foi declarada
            if (symbolTable.exists(name)) {
                errors.add("Erro: Função '" + name + "' redeclarada");
                return;
            }

            // Adiciona a função à tabela de símbolos
            List<String> paramTypes = new ArrayList<>();
            for (ParamNode param : params) {
                paramTypes.add("ANY"); // Parâmetros são implicitamente de tipo ANY
            }
            symbolTable.addFunction(name, "ANY", paramTypes); // Retorno também é implicitamente ANY

            // Salva o nome da função atual para o contexto das variáveis locais
            String previousFunction = currentFunction;
            currentFunction = name;

            // Adiciona parâmetros como variáveis locais
            for (ParamNode param : params) {
                symbolTable.add(param.getName(), "ANY", name);
            }

            // Visita o corpo da função
            visitNode(body);

            // Restaura o contexto de função anterior
            currentFunction = previousFunction;
        } catch (Exception e) {
            errors.add("Erro ao analisar declaração de função: " + e.getMessage());
        }
    }

    private void visitCallNode(ASTNode node) {
        try {
            String funcName;
            List<ASTNode> args;

            if (node instanceof CallExpr) {
                java.lang.reflect.Field nameField = CallExpr.class.getDeclaredField("functionName");
                nameField.setAccessible(true);
                funcName = (String) nameField.get(node);

                java.lang.reflect.Field argsField = CallExpr.class.getDeclaredField("args");
                argsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<ASTNode> callArgs = (List<ASTNode>) argsField.get(node);
                args = callArgs;
            } else { // CallNode
                java.lang.reflect.Method getFuncName = CallNode.class.getMethod("getFunctionName");
                funcName = (String) getFuncName.invoke(node);

                java.lang.reflect.Method getArgs = CallNode.class.getMethod("getArguments");
                @SuppressWarnings("unchecked")
                List<ASTNode> callArgs = (List<ASTNode>) getArgs.invoke(node);
                args = callArgs;
            }

            // Remove o : do início do nome se existir
            if (funcName.startsWith(":")) {
                funcName = funcName.substring(1);
            }

            // Verifica se a função existe
            if (!symbolTable.functionExists(funcName)) {
                errors.add("Erro: Chamada à função não declarada '" + funcName + "'");
                return;
            }

            // Verifica número de argumentos
            List<String> paramTypes = symbolTable.getFunctionParamTypes(funcName);
            if (paramTypes.size() != args.size()) {
                errors.add("Erro: Número incorreto de argumentos na chamada à função '" + 
                          funcName + "'. Esperado: " + paramTypes.size() + ", Recebido: " + args.size());
                return;
            }

            // Visita cada argumento
            for (ASTNode arg : args) {
                visitNode(arg);
            }

            // Verificação de tipos de argumentos está simplificada, pois PSALMS usa tipagem dinâmica
        } catch (Exception e) {
            errors.add("Erro ao analisar chamada de função: " + e.getMessage());
        }
    }

    private void visitVarNode(VarNode node) {
        String name = node.getName();
        if (name.startsWith(":")) {
            name = name.substring(1);
        }

        // Verifica se a variável foi declarada
        if (!symbolTable.exists(name)) {
            errors.add("Erro: Variável '" + name + "' utilizada mas não declarada");
        }
    }

    private void visitReturnNode(ReturnNode node) {
        try {
            java.lang.reflect.Field exprField = ReturnNode.class.getDeclaredField("expression");
            exprField.setAccessible(true);
            ASTNode expr = (ASTNode) exprField.get(node);

            // Verifica se está dentro de uma função
            if (currentFunction == null) {
                errors.add("Erro: 'amen' (return) fora de uma função");
                return;
            }

            // Visita a expressão de retorno
            visitNode(expr);
        } catch (Exception e) {
            errors.add("Erro ao analisar retorno: " + e.getMessage());
        }
    }

    private void visitIfNode(IfNode node) {
        try {
            java.lang.reflect.Field condField = IfNode.class.getDeclaredField("condicao");
            condField.setAccessible(true);
            ASTNode cond = (ASTNode) condField.get(node);

            java.lang.reflect.Field thenField = IfNode.class.getDeclaredField("thenBlock");
            thenField.setAccessible(true);
            BlockNode thenBlock = (BlockNode) thenField.get(node);

            java.lang.reflect.Field elseIfsField = IfNode.class.getDeclaredField("elseIfs");
            elseIfsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ElseIfNode> elseIfs = (List<ElseIfNode>) elseIfsField.get(node);

            java.lang.reflect.Field elseField = IfNode.class.getDeclaredField("elseBlock");
            elseField.setAccessible(true);
            BlockNode elseBlock = (BlockNode) elseField.get(node);

            // Verifica condição
            visitNode(cond);
            String condType = getExpressionType(cond);
            if (!condType.equals("BOOLEAN") && !condType.equals("ANY")) {
                errors.add("Erro: Condição do 'se' deve ser booleana, mas recebeu: " + condType);
            }

            // Visita os blocos
            visitNode(thenBlock);

            for (ElseIfNode elseIf : elseIfs) {
                visitNode(elseIf);
            }

            visitNode(elseBlock);
        } catch (Exception e) {
            errors.add("Erro ao analisar estrutura 'se': " + e.getMessage());
        }
    }

    private void visitWhileLoopNode(WhileLoopNode node) {
        try {
            java.lang.reflect.Field condField = WhileLoopNode.class.getDeclaredField("condicao");
            condField.setAccessible(true);
            ASTNode cond = (ASTNode) condField.get(node);

            java.lang.reflect.Field corpoField = WhileLoopNode.class.getDeclaredField("corpo");
            corpoField.setAccessible(true);
            BlockNode corpo = (BlockNode) corpoField.get(node);

            // Verifica condição
            visitNode(cond);
            String condType = getExpressionType(cond);
            if (!condType.equals("BOOLEAN") && !condType.equals("ANY")) {
                errors.add("Erro: Condição do 'enquanto' deve ser booleana, mas recebeu: " + condType);
            }

            // Visita o corpo
            visitNode(corpo);
        } catch (Exception e) {
            errors.add("Erro ao analisar estrutura 'enquanto': " + e.getMessage());
        }
    }

    private void visitForLoopNode(ForLoopNode node) {
        try {
            java.lang.reflect.Field inicField = ForLoopNode.class.getDeclaredField("inicializacao");
            inicField.setAccessible(true);
            ASTNode inic = (ASTNode) inicField.get(node);

            java.lang.reflect.Field condField = ForLoopNode.class.getDeclaredField("condicao");
            condField.setAccessible(true);
            ASTNode cond = (ASTNode) condField.get(node);

            java.lang.reflect.Field incField = ForLoopNode.class.getDeclaredField("incremento");
            incField.setAccessible(true);
            ASTNode inc = (ASTNode) incField.get(node);

            java.lang.reflect.Field corpoField = ForLoopNode.class.getDeclaredField("corpo");
            corpoField.setAccessible(true);
            BlockNode corpo = (BlockNode) corpoField.get(node);

            // Visita cada parte do for
            visitNode(inic);
            visitNode(cond);
            
            // Verifica se a condição é booleana
            String condType = getExpressionType(cond);
            if (!condType.equals("BOOLEAN") && !condType.equals("ANY")) {
                errors.add("Erro: Condição do 'loop' deve ser booleana, mas recebeu: " + condType);
            }
            
            visitNode(inc);
            visitNode(corpo);
        } catch (Exception e) {
            errors.add("Erro ao analisar estrutura 'loop': " + e.getMessage());
        }
    }

    private void visitSwitchNode(SwitchNode node) {
        try {
            java.lang.reflect.Field exprField = SwitchNode.class.getDeclaredField("expressao");
            exprField.setAccessible(true);
            ASTNode expr = (ASTNode) exprField.get(node);

            java.lang.reflect.Field casosField = SwitchNode.class.getDeclaredField("casos");
            casosField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<CaseNode> casos = (List<CaseNode>) casosField.get(node);

            java.lang.reflect.Field defaultField = SwitchNode.class.getDeclaredField("padrao");
            defaultField.setAccessible(true);
            ASTNode padrao = (ASTNode) defaultField.get(node);

            // Visita a expressão do switch
            visitNode(expr);
            String exprType = getExpressionType(expr);

            // Visita cada caso
            for (CaseNode caso : casos) {
                java.lang.reflect.Field valorField = CaseNode.class.getDeclaredField("valor");
                valorField.setAccessible(true);
                ASTNode valor = (ASTNode) valorField.get(caso);

                java.lang.reflect.Field corpoField = CaseNode.class.getDeclaredField("corpo");
                corpoField.setAccessible(true);
                ASTNode corpo = (ASTNode) corpoField.get(caso);

                // Visita o valor do caso
                visitNode(valor);
                String valorType = getExpressionType(valor);

                // Verificação simplificada de compatibilidade de tipos entre expressão e caso
                if (!isTypeCompatible(exprType, valorType) && !exprType.equals("ANY") && !valorType.equals("ANY")) {
                    errors.add("Erro: Incompatibilidade de tipos em 'escolha'. Expressão é do tipo " + 
                              exprType + ", mas caso é do tipo " + valorType);
                }

                // Visita o corpo do caso
                visitNode(corpo);
            }

            // Visita o caso padrão
            visitNode(padrao);
        } catch (Exception e) {
            errors.add("Erro ao analisar estrutura 'escolha': " + e.getMessage());
        }
    }

    private void visitBinOpNode(BinOpNode node) {
        try {
            java.lang.reflect.Method getLeft = BinOpNode.class.getMethod("getLeft");
            java.lang.reflect.Method getRight = BinOpNode.class.getMethod("getRight");
            java.lang.reflect.Method getOp = BinOpNode.class.getMethod("getOperator");

            ASTNode left = (ASTNode) getLeft.invoke(node);
            ASTNode right = (ASTNode) getRight.invoke(node);
            String op = (String) getOp.invoke(node);

            // Visita os operandos
            visitNode(left);
            visitNode(right);

            // Obtém os tipos dos operandos
            String leftType = getExpressionType(left);
            String rightType = getExpressionType(right);

            // Verifica compatibilidade baseada no operador
            checkOperatorTypeCompatibility(leftType, rightType, op);
        } catch (Exception e) {
            errors.add("Erro ao analisar operação binária: " + e.getMessage());
        }
    }

    private void visitPrintNode(PrintNode node) {
        try {
            java.lang.reflect.Field contentField = PrintNode.class.getDeclaredField("conteudo");
            contentField.setAccessible(true);
            ASTNode content = (ASTNode) contentField.get(node);

            // Visita o conteúdo a ser impresso
            visitNode(content);
        } catch (Exception e) {
            errors.add("Erro ao analisar comando de impressão: " + e.getMessage());
        }
    }

    private void visitInputNode(InputNode node) {
        try {
            java.lang.reflect.Field varField = InputNode.class.getDeclaredField("variavel");
            varField.setAccessible(true);
            ASTNode var = (ASTNode) varField.get(node);

            if (var instanceof VarNode) {
                VarNode varNode = (VarNode) var;
                String varName = varNode.getName();

                // Verifica se a variável existe ou cria uma nova
                if (!symbolTable.exists(varName)) {
                    symbolTable.add(varName, "STRING", currentFunction); // Entrada por padrão é string
                }
            } else {
                errors.add("Erro: Destino de input deve ser uma variável válida");
            }
        } catch (Exception e) {
            errors.add("Erro ao analisar comando de entrada: " + e.getMessage());
        }
    }

    private void visitTryCatchNode(TryCatchNode node) {
        try {
            java.lang.reflect.Field tryBlockField = TryCatchNode.class.getDeclaredField("tryBlock");
            tryBlockField.setAccessible(true);
            BlockNode tryBlock = (BlockNode) tryBlockField.get(node);

            java.lang.reflect.Field exceptionVarField = TryCatchNode.class.getDeclaredField("exceptionVar");
            exceptionVarField.setAccessible(true);
            String exceptionVar = (String) exceptionVarField.get(node);

            java.lang.reflect.Field catchBlockField = TryCatchNode.class.getDeclaredField("catchBlock");
            catchBlockField.setAccessible(true);
            BlockNode catchBlock = (BlockNode) catchBlockField.get(node);

            // Visita o bloco try
            visitNode(tryBlock);

            // Adiciona a variável de exceção ao escopo do catch
            symbolTable.add(exceptionVar, "STRING", currentFunction);

            // Visita o bloco catch
            visitNode(catchBlock);
        } catch (Exception e) {
            errors.add("Erro ao analisar estrutura 'tente-capture': " + e.getMessage());
        }
    }

    /**
     * Obtém o tipo de uma expressão.
     * @param node Nó da expressão.
     * @return Tipo da expressão (INTEGER, DECIMAL, BOOLEAN, STRING, ANY).
     */
    private String getExpressionType(ASTNode node) {
        if (node == null) return "ANY";

        try {
            if (node instanceof Num) {
                java.lang.reflect.Method getTipo = Num.class.getMethod("getTipo");
                Object tipo = getTipo.invoke(node);
                return tipo.toString();
            } else if (node instanceof BooleanNode) {
                return "BOOLEAN";
            } else if (node instanceof Str) {
                return "STRING";
            } else if (node instanceof VarNode) {
                String varName = ((VarNode) node).getName();
                if (varName.startsWith(":")) {
                    varName = varName.substring(1);
                }
                return symbolTable.exists(varName) ? symbolTable.getType(varName) : "ANY";
            } else if (node instanceof BinOpNode) {
                // Inferência de tipo baseada no operador
                java.lang.reflect.Method getOp = BinOpNode.class.getMethod("getOperator");
                java.lang.reflect.Method getLeft = BinOpNode.class.getMethod("getLeft");
                java.lang.reflect.Method getRight = BinOpNode.class.getMethod("getRight");

                String op = (String) getOp.invoke(node);
                ASTNode left = (ASTNode) getLeft.invoke(node);
                ASTNode right = (ASTNode) getRight.invoke(node);

                String leftType = getExpressionType(left);
                String rightType = getExpressionType(right);

                // Operadores de comparação -> BOOLEAN
                if (op.equals("==") || op.equals("!=") || op.equals(">") || 
                    op.equals("<") || op.equals(">=") || op.equals("<=")) {
                    return "BOOLEAN";
                }
                // Operadores lógicos -> BOOLEAN
                else if (op.equals("&&") || op.equals("||")) {
                    return "BOOLEAN";
                }
                // Operador de adição -> STRING se um dos operandos for STRING
                else if (op.equals("+")) {
                    if (leftType.equals("STRING") || rightType.equals("STRING")) {
                        return "STRING";
                    }
                    // Se um dos tipos for DECIMAL, o resultado é DECIMAL
                    else if (leftType.equals("DECIMAL") || rightType.equals("DECIMAL")) {
                        return "DECIMAL";
                    }
                    // Caso contrário, INTEGER
                    else {
                        return "INTEGER";
                    }
                }
                // Outros operadores aritméticos
                else if (op.equals("-") || op.equals("*") || op.equals("/")) {
                    // Divisão sempre resulta em DECIMAL
                    if (op.equals("/")) {
                        return "DECIMAL";
                    }
                    // Se um dos tipos for DECIMAL, o resultado é DECIMAL
                    else if (leftType.equals("DECIMAL") || rightType.equals("DECIMAL")) {
                        return "DECIMAL";
                    }
                    // Caso contrário, INTEGER
                    else {
                        return "INTEGER";
                    }
                }
            } else if (node instanceof CallExpr) {
                java.lang.reflect.Field nameField = CallExpr.class.getDeclaredField("functionName");
                nameField.setAccessible(true);
                String funcName = (String) nameField.get(node);
                if (funcName.startsWith(":")) {
                    funcName = funcName.substring(1);
                }
                
                if (symbolTable.functionExists(funcName)) {
                    return symbolTable.getFunctionReturnType(funcName);
                }
            }
        } catch (Exception e) {
            errors.add("Erro ao determinar tipo de expressão: " + e.getMessage());
        }

        // Tipo padrão quando não consegue determinar
        return "ANY";
    }

    /**
     * Verifica compatibilidade entre tipos.
     * @param type1 Primeiro tipo.
     * @param type2 Segundo tipo.
     * @return true se os tipos são compatíveis.
     */
    private boolean isTypeCompatible(String type1, String type2) {
        // Se algum for ANY, consideramos compatível (tipagem dinâmica)
        if (type1.equals("ANY") || type2.equals("ANY")) {
            return true;
        }

        // Tipos iguais são compatíveis
        if (type1.equals(type2)) {
            return true;
        }

        // Compatibilidade numérica (INTEGER pode ser atribuído a DECIMAL)
        if (type1.equals("DECIMAL") && type2.equals("INTEGER")) {
            return true;
        }

        // Incompatível
        return false;
    }

    /**
     * Verifica compatibilidade de tipos para operadores específicos.
     * @param leftType Tipo do operando esquerdo.
     * @param rightType Tipo do operando direito.
     * @param op Operador.
     */
    private void checkOperatorTypeCompatibility(String leftType, String rightType, String op) {
        // Operações com tipos ANY são sempre permitidas
        if (leftType.equals("ANY") || rightType.equals("ANY")) {
            return;
        }

        // Operadores de comparação
        if (op.equals("==") || op.equals("!=")) {
            // Todos os tipos podem ser comparados por igualdade/desigualdade
            return;
        }

        // Operadores relacionais
        if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
            // Apenas números e strings podem ser comparados
            if (!(isNumeric(leftType) && isNumeric(rightType)) && 
                !(leftType.equals("STRING") && rightType.equals("STRING"))) {
                errors.add("Erro: Operador '" + op + "' não pode ser aplicado entre tipos " + 
                          leftType + " e " + rightType);
            }
            return;
        }

        // Operadores lógicos
        if (op.equals("&&") || op.equals("||")) {
            // Apenas booleanos
            if (!leftType.equals("BOOLEAN") || !rightType.equals("BOOLEAN")) {
                errors.add("Erro: Operador '" + op + "' deve ser aplicado entre booleanos, mas recebeu " + 
                          leftType + " e " + rightType);
            }
            return;
        }

        // Operador de adição (pode ser usado para concatenação)
        if (op.equals("+")) {
            // String + qualquer coisa é permitido (concatenação)
            if (leftType.equals("STRING") || rightType.equals("STRING")) {
                return;
            }
            
            // Adição numérica
            if (!isNumeric(leftType) || !isNumeric(rightType)) {
                errors.add("Erro: Operador '+' não pode ser aplicado entre tipos " + 
                          leftType + " e " + rightType);
            }
            return;
        }

        // Outros operadores aritméticos
        if (op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
            // Apenas números
            if (!isNumeric(leftType) || !isNumeric(rightType)) {
                errors.add("Erro: Operador '" + op + "' não pode ser aplicado entre tipos " + 
                          leftType + " e " + rightType);
            }
        }
    }

    /**
     * Verifica se um tipo é numérico.
     * @param type Tipo a verificar.
     * @return true se o tipo for numérico (INTEGER ou DECIMAL).
     */
    private boolean isNumeric(String type) {
        return type.equals("INTEGER") || type.equals("DECIMAL");
    }

    /**
     * Formata os erros semânticos para exibição.
     * @return String formatada com todos os erros.
     */
    public String formatErrors() {
        if (errors.isEmpty()) {
            return "Nenhum erro semântico encontrado.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n\u001B[31m╔═══════════════════════════════════════════\u001B[0m\n");
        sb.append("\u001B[31m║ \u001B[1;31mERROS SEMÂNTICOS\u001B[0m \u001B[31m                       ║\u001B[0m\n");
        sb.append("\u001B[31m╠═══════════════════════════════════════════\u001B[0m\n");
        
        for (int i = 0; i < errors.size(); i++) {
            sb.append("\u001B[31m║ \u001B[0m").append(i + 1).append(". ").append(errors.get(i)).append("\n");
        }
        
        sb.append("\u001B[31m╚═══════════════════════════════════════════\u001B[0m\n");
        return sb.toString();
    }
}