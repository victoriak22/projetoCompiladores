# Compilador PSALMS

Projeto de **compilador didático** para a linguagem simbólica **PSALMS**: um front-end completo (léxico, sintático, semântico) escrito em **Java**, com ponte para execução real por **tradução para Pascal**. O objeto não é produzir código nativo, e sim demonstrar, de ponta a ponta, como uma linguagem de alto nível é analisada e mapeada para um alvo imediato estável (Pascal), adequado a execução com compiladores Free Pascal ou Delphi com mínimo de adaptação.

A linguagem usa **palavras-chave em português** e convenções visuais (prefixo `:` nos identificadores) para explorar os mesmos conceitos de linguagens imperativas familiares (condicionais, laços, funções, `try/catch`), com vocabulário inspirado em temática cristã. Do ponto de vista de engenharia de compiladores, PSALMS é um caso de estudo de **gramática livre de contexto** processada por **parser recursivo descendente**, **AST explícita** e **análise semântica** orientada por **tabela de símbolos** e visita em profundidade.

---

## Organização do pipeline de compilação

O fluxo implementado em `Main.java` segue quatro etapas sequenciais; qualquer falha nas três primeiras interrompe a geração de código.

### 1. Análise léxica (`Lexer.java`, pacote `Lexico`)

O código-fonte PSALMS é consumido como fluxo de caracteres com rastreamento de **linha e coluna**, útil para diagnóstico de erros. A tokenização não se apoia num único autômato monolítico: o projeto composiciona **várias máquinas finitas determinísticas (AFDs)** por classe de léxico (`CommentToken`, `PrintToken`, `InputToken`, literais de string, booleanos, palavras reservadas, operadores relacionais e aritméticos, números, delimitadores, identificadores e EOF`). A **ordem de prioridade** entre esses analisadores é deliberada: tokens mais específicos (por exemplo comentários e operadores compostos) são tentados antes de categorias genéricas, evitando captura incorreta de prefixos comuns.

O resultado é uma **lista linear de tokens** carregada pelo analisador sintático.

### 2. Análise sintática (`Parser.java`, pacote `Sintatico`)

O parser implementa **análise sintática descendente recursiva** sobre o fluxo de tokens: cada construção da gramática corresponde a um conjunto de rotinas que consomem tokens esperados e constroem nós da **árvore sintática abstrata (AST)**. A AST materializa-se como hierarquia de classes em `ast/` (comandos, expressões binárias, laços, `escolha/caso`, declaração de funções com `deus`, retorno com `amen`, e tratamento `tente`/`capturar`), com um ponto de entrada coerente com o programa (`ProgramNode`).

Erros sintáticos abortam a construção da árvore; em caso de sucesso, a AST é impressa em forma legível para inspeção (`toFormattedString`).

### 3. Análise semântica (`SemanticAnalyzer.java`, `SymbolTable.java`)

Com a AST válida, entra em cena um **analisador semântico** baseado em **visitação recursiva** dos nós. Ele mantém escopo por meio de **tabela de símbolos**, verifica declaração prévia de identificadores, coerência de tipos em expressões e atribuições, chamadas de função, fluxos de retorno e construções de controle. Erros são coletados em lista e formatados; se a lista não estiver vazia, a compilação **não prossegue** para a geração de Pascal.

### 4. Tradução para Pascal (`PascalTranslator.java`, pacote `Tradutor`)

O tradutor implementa uma estratégia em **múltiplas passagens** sobre a AST: coleta de declarações e funções, rastreamento de chamadas, inferência e registro de tipos de variáveis, e emissão de um programa Pascal estruturado (funções e bloco principal). Há cuidado explícito com variáveis de laço, duplicação de declarações e alinhamento semântico entre o modelo PSALMS e as regras de procedimentos e tipos de Pascal.

O texto gerado é gravado em **`output.pas`** na raiz do diretório de trabalho atual (mesmo nível em que o processo é iniciado).

---

## Estrutura de diretórios (visão geral)

| Caminho | Função |
|--------|--------|
| `Compilador/Main.java` | Ponto de entrada: orquestra léxico → sintático → semântico → tradução e escrita em disco |
| `Compilador/Lexico/` | Lexer, token base e AFDs por categoria léxica |
| `Compilador/Sintatico/Parser.java` | Parser recursivo descendente |
| `Compilador/ast/` | Nós da AST (comandos e expressões) |
| `Compilador/Semantico/` | Análise semântica e tabela de símbolos |
| `Compilador/Tradutor/PascalTranslator.java` | Geração de Pascal a partir da AST |
| `input.psalms` | Entrada padrão do compilador (se ausente, um exemplo embutido é usado) |
| `output.pas` | Saída gerada |
| `examples/`, `examples/erros/` | Programas PSALMS de exemplo e casos de erro |

---

## Requisitos e execução

- **JDK** 11 ou superior (recomendado: LTS atual).
- O programa usa **caminhos relativos** `input.psalms` e `output.pas`; execute o `Main` com **diretório de trabalho** na raiz do repositório (`projetoCompiladores`), onde esses arquivos residem ou serão criados.

**Compilar** todos os fontes Java (PowerShell, a partir da raiz do projeto):

```powershell
New-Item -ItemType Directory -Force -Path bin | Out-Null
$files = Get-ChildItem -Path Compilador -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d bin $files
```

**Executar:**

```powershell
java -cp bin Compilador.Main
```

Em seguida compile e execute `output.pas` com o toolchain Pascal de sua preferência (por exemplo **Free Pascal** (`fpc output.pas`)).

---

## Design da linguagem PSALMS (resumo)

Identificadores e variáveis utilizam o prefixo `:` (por exemplo `:x`, `:nome`). Funções são introduzidas por `deus`, retorno por `amen`, impressão por `p(...)`, entrada por `$ler(:variavel)`. Literais booleanos são `luz` / `trevas`. A gramática completa em notação BNF encontra-se na seção [Gramática](#gramática-formal-bnf) abaixo.

### Correspondência conceitual (Java / estilo C → PSALMS)

| Estilo tradicional | PSALMS |
|-------------------|--------|
| `if` / `else` / `else if` | `se` / `senao` / `senaose` |
| `for` / `while` | `loop` / `enquanto` |
| `break` / `continue` | `parar` / `continuar` |
| `return` | `amen` |
| `try` / `catch` | `tente` / `capturar` |
| `switch` / `case` / `default` | `escolha` / `caso` / `padrao` |
| `function` (conceitual) | `deus` |
| impressão / leitura típica | `p(...)` / `$ler(:id)` |

---

## Gramática formal (BNF)

Convenções abaixo resumem a estrutura aceita pelo parser; detalhes finos residem na implementação em `Parser.java`.

```
Programa → ListaComandos
ListaComandos → Comando ListaComandos | ε
Comando → Declaracao | Atribuicao | EstruturaCondicionalBloco | EstruturaLoop
        | EstruturaEscolha | ComandoRetorno | Impressao | LeituraEntrada
        | TratamentoErros | ChamadaFuncao | Comentario | ComandoParar

Comentario → "--" texto
Declaracao → deus :identificador -> ListaParametros { ListaComandos }
ListaParametros → :identificador | :identificador , ListaParametros | ε
Atribuicao → :identificador -> Expressao
ComandoRetorno → amen Expressao
ComandoParar → parar
Impressao → p ( Expressao )
LeituraEntrada → $ler ( :identificador )

Expressao → Valor | Expressao OperadorAritmetico Expressao | ...
OperadorAritmetico → + | - | * | / | %
OperadorRelacional → == | != | > | < | >= | <=
OperadorLogico → && | || | !

Valor → literal_numerico | literal_texto | :identificador | luz | trevas | nulo

ChamadaFuncao → :identificador ( ArgumentosFuncao )

EstruturaCondicionalBloco → se ( Expressao ) { ListaComandos } ListaSenaoSe ListaSenao
EstruturaLoop → loop ( Atribuicao ; Expressao ; Atribuicao ) { ListaComandos }
              | enquanto ( Expressao ) { ListaComandos }
EstruturaEscolha → escolha ( Expressao ) { ListaCasos CasoPadrao }
TratamentoErros → tente { ListaComandos } capturar ( :identificador ) { ListaComandos }
```

---

## Sintaxe e construções (trechos)

### Comentários

```psalms
-- comentário de linha
```

### Variáveis e atribuição

```psalms
:idade -> 25
:nome -> "Joao"
:ativo -> luz
```

### Funções e chamadas

```psalms
deus :soma -> :a, :b {
  :resultado -> :a + :b
  amen :resultado
}
:resultado -> :soma(5, 3)
```

### Entrada e saída

```psalms
p("Texto")
$ler(:nome)
```

### Condicionais e laços

```psalms
se(:idade >= 18) {
  p("Maior de idade")
} senao {
  p("Menor de idade")
}

loop(:i -> 0; :i < 5; :i -> :i + 1) {
  p(:i)
}

enquanto(:contador < 5) {
  :contador -> :contador + 1
}
```

### Escolha e exceções

```psalms
escolha(:opcao) {
  caso 1 { p("um") parar }
  padrao { p("outro") }
}

tente {
  :r -> :dividir(10, 0)
} capturar(:e) {
  p(:e)
}
```

---

## Operadores suportados

**Aritméticos:** `+`, `-`, `*`, `/`, `%`  
**Relacionais:** `==`, `!=`, `>`, `<`, `>=`, `<=`  
**Lógicos:** `&&`, `||`, `!`

---

## Exemplos de programas completos

### Função que multiplica dois valores

```psalms
deus :multiplicar -> :A, :B {
  :resultado -> :A * :B
  amen :resultado
}
:resultadoMultiplicacao -> :multiplicar(3, 4)
p("Resultado da multiplicação: ")
p(:resultadoMultiplicacao)
```

### Laço com condicional interna

```psalms
deus :listarNumeros -> :limite {
  :i -> 0
  loop(:i -> 0; :i < :limite; :i -> :i + 1) {
    se(:i % 2 == 0) {
      p("par: ") p(:i)
    } senao {
      p("impar: ") p(:i)
    }
  }
  amen :limite
}
:resultado -> :listarNumeros(5)
```

### Calculadora por `escolha`

```psalms
deus :calculadora -> :operacao, :a, :b {
  escolha(:operacao) {
    caso "soma" { amen :a + :b }
    caso "subtracao" { amen :a - :b }
    caso "multiplicacao" { amen :a * :b }
    caso "divisao" {
      se(:b == 0) { amen "Erro: divisao por zero" } senao { amen :a / :b }
    }
    padrao { amen "Operacao invalida" }
  }
}
```

### Leitura de dois números e comparação

```psalms
:n1 -> 0
:n2 -> 0
p("Digite o primeiro numero")
$ler(:n1)
p("Digite o segundo numero")
$ler(:n2)
se(:n1 > :n2) {
  p("Primeiro maior")
} senaose(:n1 < :n2) {
  p("Segundo maior")
} senao {
  p("Iguais")
}
```

---

## Diagnóstico de erros (ilustrativo)

**Léxico:** identificador de função sem `:` no nome, onde a gramática exige token de variável/função com prefixo.

**Sintático:** condicional `se` sem parênteses em torno da expressão booleana, violando a forma `se ( expr ) { ... }`.

**Semântico:** uso de variável não declarada ou combinação de tipos inválida em expressão, detectável na fase semântica antes da emissão de Pascal.

---

## Licença e uso

Este repositório é voltado a **estudo e demonstração** de técnicas de compiladores. O código gerado em Pascal deve ser compilado e executado em ambiente controlado; adapte caminhos e toolchain conforme sua máquina.

Para estender o projeto, os pontos naturais são: novos tokens e AFDs no léxico, novas produções no `Parser`, nós adicionais na AST, regras em `SemanticAnalyzer` e ramos correspondentes em `PascalTranslator`.
