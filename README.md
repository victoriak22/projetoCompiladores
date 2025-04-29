# 📖 Bem-vindo ao Compilador da Linguagem PSALMS! 🇧🇷

O **Compilador PSALMS** é um projeto educacional que realiza a **análise léxica, sintática e semântica** de uma linguagem de programação simbólica inspirada em conceitos cristãos e com palavras em português. Ele transforma o código PSALMS em uma representação intermediária que poderia ser interpretada ou traduzida para outra linguagem de mais baixo nível. Essa abordagem permite estudar conceitos fundamentais de compiladores de forma acessível para falantes de português. 🌍💻

A linguagem **PSALMS** foi projetada com uma forte influência de palavras que fazem referência ao cristianismo, tornando o código não apenas mais intuitivo, mas também com um toque simbólico e espiritual. 🙏

---

## ✝️ Transformações de Palavras Reservadas (Java → PSALMS)

| **Palavra em Java** | **Palavra em PSALMS** |
|---------------------|------------------------|
| `if`                | `se`                   |
| `else`              | `senao`                |
| `else if`           | `senaose`              |
| `for`               | `loop`                 |
| `while`             | `enquanto`             |
| `break`             | `parar`                |
| `continue`          | `continuar`            |
| `return`            | `amen`                 |
| `try`               | `tente`                |
| `catch`             | `capturar`             |
| `class`             | `alma`                 |
| `public`            | `publico`              |
| `private`           | `privado`              |
| `void`              | `vazio`                |
| `int`               | `inteiro`              |
| `float`             | `flutuante`            |
| `char`              | `caractere`            |
| `String`            | `cadeia`               |
| `true`              | `luz`                  |
| `false`             | `trevas`               |
| `null`              | `nulo`                 |
| `new`               | `gen`                  |
| `this`              | `este`                 |
| `super`             | `superior`             |
| `instanceof`        | `como`                 |
| `switch`            | `escolha`              |
| `case`              | `caso`                 |
| `default`           | `padrao`               |
| `function`          | `deus`                 |

---

## 🛠 Como Funciona o Compilador PSALMS?

O compilador PSALMS realiza um processo em três fases:

1. **Análise Léxica**: Tokenização de palavras reservadas, identificadores, operadores e literais.
2. **Análise Sintática**: Verificação da estrutura gramatical e construção da AST (Árvore Sintática Abstrata).
3. **Análise Semântica**: Verificação lógica e de tipos (uso correto de variáveis, funções, etc.).

---

## 💻 Exemplo de Código PSALMS

```psalms
deus verificarNumero(inteiro x) {
    se (x > 10) {
        amen "Maior que 10!";
    } senao {
        amen "Menor ou igual a 10!";
    }
}

deus principal() {
    cadeia nome = "Pedro";
    verificarNumero(15);
}
```

---

## 📜 Gramática da Linguagem PSALMS (BNF)

```bnf
PROGRAMA           -> INICIO

INICIO             -> COMANDO INICIO | ε

COMANDO            -> VAR_DECL
                   | ATRIB
                   | CONDICAO
                   | REPETIR
                   | FUNCAO
                   | CLASSE

-- Tipos e Identificadores
VAR_ID             -> [a-zA-Z][a-zA-Z0-9_]*
NUMERO             -> [0-9]+ | [0-9]+\.[0-9]+
TIPO_VAR           -> "int" | "flut" | "char" | "texto"

VALOR              -> VAR_ID | NUMERO
EXPRESSAO          -> EXPR_ARIT | VALOR

-- Declaração e Atribuição
VAR_DECL           -> TIPO_VAR VAR_ID "=" EXPRESSAO ";"
ATRIB              -> VAR_ID "=" EXPRESSAO ";"

-- Expressões Aritméticas
EXPR_ARIT          -> NUMERO | VAR_ID | EXPR_ARIT OPER_ARIT EXPR_ARIT
OPER_ARIT          -> "+" | "-" | "*" | "/"

-- Condições (Controle de Fluxo)
CONDICAO           -> "se" "(" EXPRESSAO ")" BLOCO CONDICAO_FIM
CONDICAO_FIM       -> "senao" BLOCO | ε

-- Estruturas de Repetição
REPETIR            -> "enquanto" "(" EXPRESSAO ")" BLOCO

-- Funções
FUNCAO             -> "func" VAR_ID "(" PARAMS ")" BLOCO
PARAMS             -> TIPO_VAR VAR_ID | TIPO_VAR VAR_ID "," PARAMS | ε

-- Bloco de Código
BLOCO              -> "{" INICIO "}"
```
