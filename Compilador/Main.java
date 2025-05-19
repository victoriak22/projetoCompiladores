package Compilador;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Compilador.Lexico.Lexer;
import Compilador.Lexico.Token;
import Compilador.Sintatico.Parser;
import Compilador.Tradutor.PascalTranslator;
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
        // Análise Léxica
        printHeader("ANALISE LEXICA");
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.getTokens();
        printTokens(tokens);
        
        // Análise Sintática
        printHeader("ANALISE SINTATICA");
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();
        
        if (ast != null) {
            printAst(ast);
            
            // Nova parte: Tradução para Pascal
            printHeader("TRADUÇÃO PARA PASCAL");
            String pascalCode = PascalTranslator.translate(ast);
            System.out.println(pascalCode);
            
            // Salvar em arquivo
            try (FileWriter writer = new FileWriter("output.pas")) {
                writer.write(pascalCode);
            }
            System.out.println("[OK] Código Pascal salvo em: output.pas");
        } else {
            printError("Erros sintáticos encontrados");
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + title + ":");
        System.out.println("========================================");
    }

    private static void printTokens(List<Token> tokens) {
        System.out.println("Tokens gerados (" + tokens.size() + "):");
        tokens.forEach(System.out::println);
        System.out.println("[OK] Análise léxica concluída");
    }

    private static void printAst(ASTNode ast) {
        System.out.println("[OK] Programa válido! AST construída.");
        System.out.println("\nÁRVORE SINTÁTICA ABSTRATA (AST):");
        System.out.println("========================================");
        System.out.println(ast.toFormattedString());
        System.out.println("========================================");
    }

    private static void printError(String message) {
        System.err.println("[ERRO] " + message);
    }
}