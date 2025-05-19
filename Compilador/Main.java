package Compilador;

import java.io.IOException;
import java.util.List;
import Compilador.Lexico.Lexer;
import Compilador.Lexico.Token;
import Compilador.Sintatico.Parser;
import Compilador.ast.ASTNode;

public class Main {
    private static final String SAMPLE_CODE = 
        "deus :multiplicar -> :A, :B {\n" +
        "  :resultado -> :A * :B\n" +
        "  amen :resultado\n" +
        "}\n" +
        "\n" +
        ":resultadoMultiplicacao -> :multiplicar(3, 4)\n" +
        "=\"'Resultado da multiplicação: ' + :resultadoMultiplicacao\"\n";

    public static void main(String[] args) {
        try {
            compile(SAMPLE_CODE);
        } catch (Exception e) {
            System.err.println("\n[ERRO] Durante a compilação:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void compile(String sourceCode) throws IOException {
        printHeader("ANALISE LEXICA");
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.getTokens();
        printTokens(tokens);
        
        printHeader("ANALISE SINTATICA");
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();
        
        if (ast != null) {
            printAst(ast);
        } else {
            printError("Erros sintaticos encontrados");
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + title + ":");
        printSeparator();
    }

    private static void printTokens(List<Token> tokens) {
        System.out.println("Tokens gerados (" + tokens.size() + "):");
        tokens.forEach(t -> System.out.println("" + t));
        printSuccess("Analise lexica concluida");
    }

    private static void printAst(ASTNode ast) {
        printSuccess("Programa valido! AST construida.");
        System.out.println("\nARVORE SINTATICA ABSTRATA (AST):");
        printSeparator();
        System.out.println(ast.toFormattedString());
        printSeparator();
    }

    private static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    private static void printError(String message) {
        System.err.println("[ERRO] " + message);
    }

    private static void printSeparator() {
        System.out.println("========================================");
    }
}