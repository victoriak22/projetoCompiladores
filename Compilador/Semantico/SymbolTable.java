package Compilador.Semantico;

import java.util.*;

/**
 * Tabela de Símbolos para o analisador semântico.
 * Armazena informações sobre variáveis e funções.
 */
public class SymbolTable {
    // Armazena variáveis: nome -> [tipo, escopo]
    private Map<String, String[]> variables;
    
    // Armazena funções: nome -> [tipoRetorno, [tipos dos parâmetros...]]
    private Map<String, List<String>> functions;

    public SymbolTable() {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
    }

    /**
     * Limpa a tabela de símbolos.
     */
    public void clear() {
        variables.clear();
        functions.clear();
    }

    /**
     * Adiciona uma variável à tabela de símbolos.
     * @param name Nome da variável.
     * @param type Tipo da variável.
     * @param scope Escopo da variável (nome da função ou null para global).
     */
    public void add(String name, String type, String scope) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        variables.put(name, new String[] { type, scope == null ? "global" : scope });
    }

    /**
     * Verifica se uma variável existe na tabela de símbolos.
     * @param name Nome da variável.
     * @return true se a variável existe.
     */
    public boolean exists(String name) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        return variables.containsKey(name);
    }

    /**
     * Obtém o tipo de uma variável.
     * @param name Nome da variável.
     * @return Tipo da variável ou "ANY" se não encontrada.
     */
    public String getType(String name) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        
        String[] info = variables.get(name);
        return info != null ? info[0] : "ANY";
    }

    /**
     * Obtém o escopo de uma variável.
     * @param name Nome da variável.
     * @return Escopo da variável ou null se não encontrada.
     */
    public String getScope(String name) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        
        String[] info = variables.get(name);
        return info != null ? info[1] : null;
    }

    /**
     * Verifica se uma variável está em um escopo acessível.
     * @param name Nome da variável.
     * @param currentScope Escopo atual.
     * @return true se a variável está acessível.
     */
    public boolean isAccessible(String name, String currentScope) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        
        if (!exists(name)) {
            return false;
        }

        String scope = getScope(name);
        return "global".equals(scope) || (currentScope != null && currentScope.equals(scope));
    }

    /**
     * Adiciona uma função à tabela de símbolos.
     * @param name Nome da função.
     * @param returnType Tipo de retorno da função.
     * @param paramTypes Lista de tipos dos parâmetros.
     */
    public void addFunction(String name, String returnType, List<String> paramTypes) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        
        List<String> functionInfo = new ArrayList<>();
        functionInfo.add(returnType);
        functionInfo.addAll(paramTypes);
        functions.put(name, functionInfo);
    }

    /**
     * Verifica se uma função existe na tabela de símbolos.
     * @param name Nome da função.
     * @return true se a função existe.
     */
    public boolean functionExists(String name) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        return functions.containsKey(name);
    }

    /**
     * Obtém o tipo de retorno de uma função.
     * @param name Nome da função.
     * @return Tipo de retorno da função ou "ANY" se não encontrada.
     */
    public String getFunctionReturnType(String name) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        
        List<String> info = functions.get(name);
        return info != null && !info.isEmpty() ? info.get(0) : "ANY";
    }

    /**
     * Obtém a lista de tipos dos parâmetros de uma função.
     * @param name Nome da função.
     * @return Lista de tipos dos parâmetros ou lista vazia se não encontrada.
     */
    public List<String> getFunctionParamTypes(String name) {
        // Normaliza o nome (remove ":" se presente)
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        
        List<String> info = functions.get(name);
        return info != null && info.size() > 1 ? info.subList(1, info.size()) : Collections.emptyList();
    }

    /**
     * Obtém um resumo das informações na tabela de símbolos.
     * @return String formatada com informações da tabela.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== TABELA DE SÍMBOLOS =====\n");
        
        // Variáveis
        sb.append("VARIÁVEIS:\n");
        for (Map.Entry<String, String[]> entry : variables.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" -> Tipo: ")
              .append(entry.getValue()[0]).append(", Escopo: ")
              .append(entry.getValue()[1]).append("\n");
        }
        
        // Funções
        sb.append("\nFUNÇÕES:\n");
        for (Map.Entry<String, List<String>> entry : functions.entrySet()) {
            List<String> info = entry.getValue();
            String returnType = info.isEmpty() ? "ANY" : info.get(0);
            List<String> paramTypes = info.size() > 1 ? info.subList(1, info.size()) : Collections.emptyList();
            
            sb.append("  ").append(entry.getKey()).append(" -> Retorna: ")
              .append(returnType).append(", Parâmetros: ")
              .append(paramTypes).append("\n");
        }
        
        return sb.toString();
    }
}