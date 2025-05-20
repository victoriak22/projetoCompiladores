# 📖 Compilador da Linguagem PSALMS 🇧🇷

## Índice de Navegação

- [📖 Introdução](#-introdução)
- [✝️ Palavras Reservadas](#️-palavras-reservadas-java--psalms)
- [🛠 Como Funciona o Compilador](#-como-funciona-o-compilador-psalms)
- [📝 Sintaxe Básica](#-sintaxe-básica-da-linguagem-psalms)
  - [Comentários](#comentários)
  - [Variáveis](#variáveis)
  - [Funções](#funções)
  - [Chamada de Funções](#chamada-de-funções)
  - [Impressão](#impressão)
- [🔄 Estruturas de Controle](#-estruturas-de-controle)
  - [Condicionais](#condicionais)
  - [Loops](#loops)
  - [Estrutura Escolha-Caso](#estrutura-escolha-caso)
- [⚠️ Tratamento de Exceções](#️-tratamento-de-exceções)
- [📑 Gramática da Linguagem](#-gramática-da-linguagem-psalms)
- [💻 Exemplos Completos](#-exemplos-completos)
  - [Exemplo 1: Função de Multiplicação](#exemplo-1-função-simples-de-multiplicação)
  - [Exemplo 2: Loop com Condicional](#exemplo-2-loop-com-condicional)
  - [Exemplo 3: Calculadora Simples](#exemplo-3-calculadora-simples)
- [🚀 Como Usar o Compilador](#-como-usar-o-compilador)
- [📚 Operadores Suportados](#-operadores-suportados)
  - [Aritméticos](#aritméticos)
  - [Relacionais](#relacionais)
  - [Lógicos](#lógicos)

---

## 📖 Introdução

O **Compilador PSALMS** é um projeto educacional que realiza a **análise léxica, sintática e semântica** de uma linguagem de programação simbólica inspirada em conceitos cristãos e com palavras em português. Ele transforma o código PSALMS em uma representação intermediária que poderia ser interpretada ou traduzida para outra linguagem de mais baixo nível como Pascal. Essa abordagem permite estudar conceitos fundamentais de compiladores de forma acessível para falantes de português. 🌍💻

A linguagem **PSALMS** foi projetada com uma forte influência de palavras que fazem referência ao cristianismo, tornando o código não apenas mais intuitivo, mas também com um toque simbólico e espiritual. 🙏

---

## ✝️ Palavras Reservadas (Java → PSALMS)

| **Palavra em Java**  | **Palavra em PSALMS** | **Descrição**                   |
| -------------------- | --------------------- | ------------------------------- |
| `if`                 | `se`                  | Estrutura condicional           |
| `else`               | `senao`               | Alternativa à condição          |
| `else if`            | `senaose`             | Condição alternativa            |
| `for`                | `loop`                | Loop com contador               |
| `while`              | `enquanto`            | Loop com condição               |
| `break`              | `parar`               | Interrompe um loop              |
| `continue`           | `continuar`           | Pula para próxima iteração      |
| `return`             | `amen`                | Retorna um valor de função      |
| `try`                | `tente`               | Bloco de tratamento de exceções |
| `catch`              | `capturar`            | Captura exceções                |
| `class`              | `alma`                | Define uma classe               |
| `public`             | `publico`             | Modificador de acesso público   |
| `private`            | `privado`             | Modificador de acesso privado   |
| `void`               | `vazio`               | Função sem retorno              |
| `int`                | `inteiro`             | Tipo de dado inteiro            |
| `float`              | `flutuante`           | Tipo de dado decimal            |
| `char`               | `caractere`           | Tipo de dado caractere          |
| `String`             | `cadeia`              | Tipo de dado texto              |
| `true`               | `luz`                 | Valor booleano verdadeiro       |
| `false`              | `trevas`              | Valor booleano falso            |
| `null`               | `nulo`                | Valor nulo                      |
| `new`                | `gen`                 | Cria nova instância             |
| `this`               | `este`                | Referência ao objeto atual      |
| `super`              | `superior`            | Referência à classe pai         |
| `instanceof`         | `como`                | Verifica tipo de objeto         |
| `switch`             | `escolha`             | Estrutura de múltipla escolha   |
| `case`               | `caso`                | Caso em estrutura escolha       |
| `default`            | `padrao`              | Caso padrão                     |
| `function`           | `deus`                | Define uma função               |
| `System.out.println` | `=`                   | Imprime na saída padrão         |

---

## 🛠 Como Funciona o Compilador PSALMS?

O compilador PSALMS realiza um processo em quatro fases:

1. **Análise Léxica**: Tokenização de palavras reservadas, identificadores, operadores e literais.
2. **Análise Sintática**: Verificação da estrutura gramatical e construção da AST (Árvore Sintática Abstrata).
3. **Análise Semântica**: Verificação lógica e de tipos (uso correto de variáveis, funções, etc.).
4. **Tradução**: Conversão do código PSALMS para código Pascal equivalente.

---

## 📝 Sintaxe Básica da Linguagem PSALMS

### Comentários

```psalms
-- Este é um comentário de linha única
```

### Variáveis

Em PSALMS, todas as variáveis começam com `:` (dois pontos).

```psalms
:idade -> 25
:nome -> "João"
:ativo -> luz
```

### Funções

As funções são declaradas com a palavra-chave `deus`.

```psalms
deus :soma -> :a, :b {
  :resultado -> :a + :b
  amen :resultado
}
```

### Chamada de Funções

```psalms
:resultado -> :soma(5, 3)
```

### Impressão

Para imprimir valores, use a sintaxe `=`:

```psalms
p("Texto a ser exibido")
p("Resultado: ")
p(:variavel)
```

---

## 🔄 Estruturas de Controle

### Condicionais

```psalms
se(:idade >= 18){
  p("Maior de idade")
senao{
  p("Menor de idade")
  }
}
```

Com múltiplas condições:

```psalms
se(:nota >= 7){
  p("Aprovado")

senaose(:nota >= 5){
  p("Recuperação")
}
senao{
  p("Reprovado")
}
}
```

### Loops

Loop for:

```psalms
loop(:i -> 0; :i < 5; :i -> :i + 1) {
  p("Valor de i: ")
  p(:i)
}
```

Loop while:

```psalms
:contador -> 0
enquanto(:contador < 5) {
  p("Contador: ")
  p(:contador)
  :contador -> :contador + 1
}
```

### Estrutura Escolha-Caso

```psalms
escolha(:opcao) {
  caso 1{
    p("Opção 1 selecionada")
    parar
  }
  caso 2{
    p("Opção 2 selecionada")
    parar
  }
  padrao{
    p("Opção inválida")
  }
}
```

---

## ⚠️ Tratamento de Exceções

```psalms
tente {
  :resultado -> :dividir(10, 0)
  p("Resultado: ")
  p(:resultado)
}
capturar(:erro){
  p("Ocorreu um erro: ")
  p(:erro)
  }
```

---

## 📑 Gramática da Linguagem PSALMS

```bnf
Programa → ListaComandos

ListaComandos → Comando ListaComandos | ε

Comando → Declaracao
        | Atribuicao
        | EstruturaCondicionalBloco
        | EstruturaLoop
        | EstruturaEscolha
        | ComandoRetorno
        | Impressao
        | TratamentoErros
        | ChamadaFuncao
        | Comentario
        | ComandoParar

Comentario → "--" texto

Declaracao → deus :identificador -> ListaParametros { ListaComandos }

ListaParametros → :identificador | :identificador , ListaParametros | ε

Atribuicao → :identificador -> Expressao

ComandoRetorno → amen Expressao

ComandoParar → parar

Impressao → = Expressao
          | = Expressao Impressao  -- Para suportar múltiplas impressões em sequência

Expressao → Valor
          | Expressao OperadorAritmetico Expressao
          | Expressao OperadorRelacional Expressao
          | Expressao OperadorLogico Expressao
          | ( Expressao )
          | ChamadaFuncao

OperadorAritmetico → + | - | * | / | %
OperadorRelacional → == | != | > | < | >= | <=
OperadorLogico → && | || | !

Valor → literal_numerico
      | literal_texto
      | :identificador
      | luz
      | trevas
      | nulo

ChamadaFuncao → :identificador ( ArgumentosFuncao )
ArgumentosFuncao → ListaArgumentos | ε
ListaArgumentos → Expressao | Expressao , ListaArgumentos

EstruturaCondicionalBloco → se ( Expressao ) { ListaComandos } ListaSenaoSe ListaSenao

ListaSenaoSe → SenaoSe ListaSenaoSe | ε

SenaoSe → senaose ( Expressao ) { ListaComandos }

ListaSenao → Senao | ε
Senao → senao { ListaComandos }

EstruturaLoop → loop ( Atribuicao ; Expressao ; Atribuicao ) { ListaComandos }
              | enquanto ( Expressao ) { ListaComandos }

EstruturaEscolha → escolha ( Expressao ) {
                     ListaCasos
                     CasoPadrao
                   }

ListaCasos → caso Valor { ListaComandos } ListaCasos | ε

CasoPadrao → padrao { ListaComandos } | ε

TratamentoErros → tente { ListaComandos } capturar ( :identificador ) { ListaComandos }
```

---

## 💻 Exemplos Completos

### Exemplo 1: Função Simples de Multiplicação

```psalms
deus :multiplicar -> :A, :B {
  :resultado -> :A * :B
  amen :resultado
}

:resultadoMultiplicacao -> :multiplicar(3, 4)
p("Resultado da multiplicação: ")
p(:resultadoMultiplicacao)
```

### Exemplo 2: Loop com Condicional

```psalms
deus :listarNumeros -> :limite {
  -- Declaração de variável de controle
  :i -> 0

  -- Loop que vai de 0 até o limite
  loop(:i -> 0; :i < :limite; :i -> :i + 1) {
    -- Verifica se o número é par ou ímpar
    se(:i % 2 == 0){
      p("Número par: ")
      p(:i)
    senao{
      p("Número ímpar: ")
      p(:i)
    }
  }
}

  amen :limite
}

-- Chamada da função com limite 5
:resultado -> :listarNumeros(5)
```

### Exemplo 3: Calculadora Simples

```psalms
deus :calculadora -> :operacao, :a, :b{
  escolha(:operacao){
    caso "soma"{
      amen :a + :b
    }
    caso "subtracao"{
      amen :a - :b
    }
    caso "multiplicacao"{
      amen :a * :b
    }
    caso "divisao"{
      se(:b == 0){
        amen "Erro: Divisão por zero"
      }
      senao{
        amen :a / :b
      }
    }
    padrao{
      amen "Operação inválida"
    }
  }
}

:resSoma -> :calculadora("soma", 10, 5)
:resSubtracao -> :calculadora("subtracao", 10, 5)
:resMultiplicacao -> :calculadora("multiplicacao", 10, 5)
:resDivisao -> :calculadora("divisao", 10, 5)

p("Soma: ")
p(:resSoma)
p("Subtração: ")
p(:resSubtracao)
p("Multiplicação: ")
p(:resMultiplicacao)
p("Divisão: ")
p(:resDivisao)
```
### Exemplo 4: Loop que imprime até o número 10

```psalms
deus :ateDez {
  :i -> 1

  loop(:i -> 1; :i <= 10; :i -> :i + 1){
    p("Número atual: ")
    p(:i)
  }

  amen nulo
}

-- Chamada da função
:resultado -> :ateDez()
```
### Exemplo 5: Função que retorna o maior e o menor entre dois números

```psalms
deus :maiorMenor -> :a, :b {
  -- Verifica qual é o maior
  se(:a > :b){
    :maior -> :a
    :menor -> :b
  }
  senao{
    :maior -> :b
    :menor -> :a
  }

  p("Maior número: ")
  p(:maior)
  p("Menor número: ")
  p(:menor)

  amen nulo
}

-- Chamada da função
:resultado -> :maiorMenor(8, 3)
```

Para mais exemplos, consulte o arquivo `exemplos_psalms.psalms` incluído no projeto.

---

## 🚀 Como Usar o Compilador

1. Clone este repositório:

   ```
   git clone https://github.com/seu-usuario/compilador-psalms.git
   ```

2. Compile o projeto:

   ```
   javac Compilador/Main.java
   ```

3. Crie um arquivo `input.psalms` com seu código PSALMS.

4. Execute o compilador:

   ```
   java Compilador.Main
   ```

5. O compilador gerará um arquivo `output.pas` com o código Pascal equivalente.

---

## 📚 Operadores Suportados

### Aritméticos

- `+` : Adição
- `-` : Subtração
- `*` : Multiplicação
- `/` : Divisão
- `%` : Módulo (resto)

### Relacionais

- `==` : Igual a
- `!=` : Diferente de
- `>` : Maior que
- `<` : Menor que
- `>=` : Maior ou igual a
- `<=` : Menor ou igual a

### Lógicos

- `&&` : E lógico
- `||` : OU lógico
- `!` : NÃO lógico
