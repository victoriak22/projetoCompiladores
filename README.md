# 📖 Compilador da Linguagem PSALMS 🇧🇷

## Índice de Navegação

- [📖 Introdução](#-introdução)
- [✝️ Palavras Reservadas](#️-palavras-reservadas-java--psalms)
- [🛠 Como Funciona o Compilador](#-como-funciona-o-compilador-psalms)
  - [Análise Léxica](#análise-léxica)
  - [Análise Sintática](#análise-sintática)
  - [Análise Semântica](#análise-semântica)
  - [Tradução](#tradução)
- [📝 Sintaxe Básica](#-sintaxe-básica-da-linguagem-psalms)
  - [Comentários](#comentários)
  - [Variáveis](#variáveis)
  - [Funções](#funções)
  - [Chamada de Funções](#chamada-de-funções)
  - [Impressão](#impressão)
  - [Leitura de Entrada](#leitura-de-entrada)
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
  - [Exemplo 4: Programa com Entrada do Usuário](#exemplo-4-programa-com-entrada-do-usuário)
  - [Exemplo 5: Loop até 10](#exemplo-5-loop-que-imprime-até-o-número-10)
  - [Exemplo 6: Função para Maior e Menor](#exemplo-6-função-que-retorna-o-maior-e-o-menor-entre-dois-números)
- [❌ Exemplos de Erros](#-exemplos-de-erros)
  - [Erro Léxico](#erro-léxico--identificador-sem-dois-pontos)
  - [Erro Sintático](#erro-sintático--condicional-sem-parênteses)
  - [Erro Semântico](#erro-semântico--divisão-por-zero)
- [📚 Operadores Suportados](#-operadores-suportados)
  - [Aritméticos](#aritméticos)
  - [Relacionais](#relacionais)
  - [Lógicos](#lógicos)
---

## 📖 Introdução

O **Compilador PSALMS** é um projeto educacional que realiza a **análise léxica, sintática e semântica** de uma linguagem de programação simbólica inspirada em conceitos cristãos e com palavras em português. Ele transforma o código PSALMS em uma representação intermediária que é traduzida para Pascal, permitindo a execução real do código. Essa abordagem permite estudar conceitos fundamentais de compiladores de forma acessível para falantes de português. 🌍💻

A linguagem **PSALMS** foi projetada com uma forte influência de palavras que fazem referência ao cristianismo, tornando o código não apenas mais intuitivo para falantes de português, mas também com um toque simbólico e espiritual. 🙏

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
| `System.out.println` | `p()`                 | Imprime na saída padrão         |
| `Scanner.nextLine`   | `$ler()`              | Lê entrada do usuário           |

---

## 🛠 Como Funciona o Compilador PSALMS?

O compilador PSALMS realiza um processo em quatro fases:

### Análise Léxica
- Tokenização de palavras reservadas, identificadores, operadores e literais
- Implementada na classe `Lexer.java` utilizando Autômatos Finitos Determinísticos (AFDs)
- Cada token é classificado por tipo (ID, NUMBER, OPERATOR, etc.)
- Localiza e reporta erros léxicos com mensagens descritivas

### Análise Sintática
- Verifica a estrutura gramatical do programa
- Implementada em `Parser.java` usando análise descendente recursiva
- Constrói a AST (Árvore Sintática Abstrata) se o programa for válido
- Fornece mensagens de erro detalhadas para problemas sintáticos

### Análise Semântica
- Verifica a lógica e tipos (uso correto de variáveis, funções, etc.)
- Usa a tabela de símbolos para rastrear identificadores e seus tipos
- Verifica se variáveis são declaradas antes do uso
- Valida compatibilidade de tipos em operações e atribuições
- Detecção de erros como variáveis não declaradas e incompatibilidade de tipos

### Tradução
- Converte a AST em código Pascal equivalente
- Implementa a inferência de tipos para variáveis
- Otimiza estruturas de controle para código Pascal mais limpo
- Gera um programa Pascal completo e pronto para execução

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

Para imprimir valores, use a sintaxe `p()`:

```psalms
p("Texto a ser exibido")
p(:variavel)
```

### Leitura de Entrada

Para ler valores do usuário, use a sintaxe `$ler()`:

```psalms
p("Digite seu nome:")
$ler(:nome)

p("Digite sua idade:")
$ler(:idade)
```

---

## 🔄 Estruturas de Controle

### Condicionais

```psalms
se(:idade >= 18) {
  p("Maior de idade")
} senao {
  p("Menor de idade")
}
```

Com múltiplas condições:

```psalms
se(:nota >= 7) {
  p("Aprovado")
} senaose(:nota >= 5) {
  p("Recuperação")
} senao {
  p("Reprovado")
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
  caso 1 {
    p("Opção 1 selecionada")
    parar
  }
  caso 2 {
    p("Opção 2 selecionada")
    parar
  }
  padrao {
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
} capturar(:erro) {
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
        | LeituraEntrada
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

Impressao → p ( Expressao )

LeituraEntrada → $ler ( :identificador )

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
    se(:i % 2 == 0) {
      p("Número par: ")
      p(:i)
    } senao {
      p("Número ímpar: ")
      p(:i)
    }
  }
  
  amen :limite
}

-- Chamada da função com limite 5
:resultado -> :listarNumeros(5)
```

### Exemplo 3: Calculadora Simples

```psalms
deus :calculadora -> :operacao, :a, :b {
  escolha(:operacao) {
    caso "soma" {
      amen :a + :b
    }
    caso "subtracao" {
      amen :a - :b
    }
    caso "multiplicacao" {
      amen :a * :b
    }
    caso "divisao" {
      se(:b == 0) {
        amen "Erro: Divisão por zero"
      } senao {
        amen :a / :b
      }
    }
    padrao {
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

### Exemplo 4: Programa com Entrada do Usuário

```psalms
-- Programa simples com entrada do usuário

:n1 -> 0
:n2 -> 0

p("Digite o primeiro numero")
$ler(:n1)

p("Digite o segundo numero")
$ler(:n2)

p("Voce digitou os numeros:")
p(:n1)
p(:n2)

se(:n1 > :n2) {
    p("O primeiro numero e maior que o segundo")
} senaose(:n1 < :n2) {
    p("O segundo numero e maior que o primeiro")
} senao {
    p("Os dois numeros sao iguais")
}

p("Fim do programa")
```

### Exemplo 5: Loop que imprime até o número 10

```psalms
:i -> 1

loop(:i -> 1; :i <= 10; :i -> :i + 1) {
  p("Numero atual: ")
  p(:i)
}
```

### Exemplo 6: Função que retorna o maior e o menor entre dois números

```psalms
-- Declaração dos valores
:a -> 8
:b -> 3
:maior -> 0
:menor -> 0

-- Verifica se os números são iguais
se(:a == :b) {
  p("Os numeros sao iguais: ")
  p(:a)
} senao {
  -- Se não forem iguais, verifica qual eh o maior
  se(:a > :b) {
    :maior -> :a
    :menor -> :b
  } senao {
    :maior -> :b
    :menor -> :a
  }
  
  p("Maior numero: ")
  p(:maior)
  p("Menor numero: ")
  p(:menor)
}
```

## ❌ Exemplos de Erros

### Erro Léxico – Identificador sem dois-pontos

```psalms
deus multiplicar -> :a, :b {
  :resultado -> :a * :b
  amen :resultado
}
```
**Erro:** O nome multiplicar não está precedido por :. Todos os identificadores na linguagem PSALMS devem começar com : para serem reconhecidos como válidos.

### Erro Sintático – Condicional sem parênteses

```psalms
se :x > 0 {
  p("Positivo")
}
```
**Erro:** A estrutura se requer que a expressão condicional esteja entre parênteses.

### Erro Semântico – Variável não declarada

```psalms
:x -> 10
:y -> :x + :z
p(:y)
```
**Erro:** A variável :z é utilizada mas não foi declarada anteriormente. O analisador semântico detecta este tipo de erro.

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