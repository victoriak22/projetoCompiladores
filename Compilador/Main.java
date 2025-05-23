package Compilador;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Compilador.Lexico.Lexer;
import Compilador.Lexico.Token;
import Compilador.Semantico.SemanticAnalyzer;
import Compilador.Sintatico.Parser;
import Compilador.Tradutor.PascalTranslator;
import Compilador.ast.ASTNode;

public class Main {
    // Código de exemplo mantido como fallback caso o arquivo não seja encontrado
    private static final String SAMPLE_CODE = "deus :multiplicar -> :A, :B {\n" +
            "  :resultado -> :A * :B\n" +
            "  amen :resultado\n" +
            "}\n" +
            "\n" +
            ":resultadoMultiplicacao -> :multiplicar(3, 6)\n" +
            "=\"'Resultado da multiplicação: '\" + :resultadoMultiplicacao\n";

    private static final String INPUT_FILE = "input.psalms";
    private static final String OUTPUT_FILE = "output.pas";

    public static void main(String[] args) {
        try {
            // Tenta ler o código fonte do arquivo de entrada
            String sourceCode = readSourceFile(INPUT_FILE);

            // Compila o código PSALMS para Pascal
            compile(sourceCode);

            System.out.println("[INFO] Compilação concluída com sucesso!");
        } catch (Exception e) {
            System.err.println("\n[ERRO] Durante a compilação:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Lê o conteúdo do arquivo fonte.
     * Se o arquivo não existir, usa o código de exemplo.
     */
    private static String readSourceFile(String filename) throws IOException {
        Path filePath = Paths.get(filename);

        if (Files.exists(filePath)) {
            System.out.println("[INFO] Lendo arquivo de entrada: " + filename);
            return Files.readString(filePath);
        } else {
            System.out.println("[AVISO] Arquivo " + filename + " não encontrado. Usando código de exemplo.");
            return SAMPLE_CODE;
        }
    }

    private static String compile(String sourceCode) throws IOException {
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

            // Análise Semântica (NOVO)
            printHeader("ANALISE SEMANTICA");
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            List<String> semanticErrors = semanticAnalyzer.analyze(ast);
            
            if (!semanticErrors.isEmpty()) {
                System.out.println(semanticAnalyzer.formatErrors());
                printError("Erros semânticos encontrados, abortando compilação.");
                return null;
            } else {
                System.out.println("[OK] Análise semântica concluída sem erros.");
            }

            // Tradução para Pascal
            printHeader("TRADUÇÃO PARA PASCAL");
            String pascalCode = PascalTranslator.translate(ast);
            System.out.println(pascalCode);

            // Salvar em arquivo
            try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
                writer.write(pascalCode);
            }
            System.out.println("[OK] Código Pascal salvo em: " + OUTPUT_FILE);

            return OUTPUT_FILE;
        } else {
            printError("Erros sintáticos encontrados");
            return null;
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
    }

    private static void printError(String message) {
        System.err.println("[ERRO] " + message);
    }
}