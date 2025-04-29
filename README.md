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
PROGRAMA           -> BLOCO

BLOCO              -> COMANDO BLOCO | ε

COMANDO            -> DECLARACAO
                   | ATRIBUICAO
                   | DECISAO
                   | REPETICAO
                   | RETORNO
                   | FUNCAO
                   | CLASSE

-- Elementos léxicos
ID                 -> [a-zA-Z][a-zA-Z0-9_]*
NUM                -> [0-9]+ | [0-9]+\.[0-9]+
BOOLEAN            -> "luz" | "trevas"
TIPO               -> "inteiro" | "flutuante" | "caractere" | "cadeia" | "bool"

VALOR              -> ID | NUM | BOOLEAN | "nulo"
EXPRESSAO          -> EXPR_ARIT | EXPR_LOGICA | VALOR

-- Declarações e atribuições
DECLARACAO         -> TIPO ID "=" EXPRESSAO ";"
ATRIBUICAO         -> ID OPER_ATRIB EXPRESSAO ";"
OPER_ATRIB         -> "=" | "+=" | "-="

INCREMENTO         -> ID OPER_INC ";"
OPER_INC           -> "++" | "--"

-- Expressões Aritméticas
EXPR_ARIT          -> TERMO | "(" EXPR_ARIT ")" TERMO
TERMO              -> FATOR | TERMO OPER_MAT FATOR
FATOR              -> VALOR | "(" EXPR_ARIT ")"
OPER_MAT           -> "+" | "-" | "*" | "/"

-- Expressões Lógicas
EXPR_LOGICA        -> EXPR_REL EXPR_LOG_CONT
EXPR_LOG_CONT      -> OPER_LOGICO EXPR_REL EXPR_LOG_CONT | ε

EXPR_REL           -> EXPR_ARIT OPER_REL EXPR_ARIT
                   | "(" EXPR_LOGICA ")"
                   | "nao" EXPR_LOGICA
                   | BOOLEAN

OPER_REL           -> ">" | "<" | "==" | "!=" | "<=" | ">="
OPER_LOGICO        -> "e" | "ou"

-- Controle de Fluxo
DECISAO            -> "se" "(" EXPR_LOGICA ")" BLOCO_ENCAP DECISAO_AUX
DECISAO_AUX        -> "senaose" "(" EXPR_LOGICA ")" BLOCO_ENCAP DECISAO_AUX
                   | "senao" BLOCO_ENCAP
                   | ε

-- Estruturas de Repetição
REPETICAO          -> ENQUANTO | PARA

ENQUANTO           -> "enquanto" "(" EXPR_LOGICA ")" BLOCO_ENCAP

PARA               -> "loop" "(" DECLARACAO ";" EXPR_LOGICA ";" PARA_ATUAL ")" BLOCO_ENCAP
PARA_ATUAL         -> ATRIBUICAO | INCREMENTO

-- Funções
FUNCAO             -> "deus" ID "(" PARAMS ")" BLOCO_ENCAP
PARAMS             -> PARAM PARAM_CONT | ε
PARAM              -> TIPO ID
PARAM_CONT         -> "," PARAM PARAM_CONT | ε

-- Retorno
RETORNO            -> "amen" EXPRESSAO ";"
```
