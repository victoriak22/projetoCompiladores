public class Main {
    public static void main(String[] args) throws IOException {
        String data = "\tdeus verificarNumero(inteiro x) { se (x > 10): amen \"Maior que 10!\" senao: amen 'Menor ou igual a 10!' }";
        Lexer lexer = new Lexer(data);
        List<Token> tokens = lexer.getTokens();
        tokens.add(new Token("EOF", "EOF")); // Adicione EOF ao final
        for (Token t : tokens) {
            System.out.println(t);
        }

        Parser parser = new Parser(tokens);
        parser.main(); // inicia análise sintática
    }
}
