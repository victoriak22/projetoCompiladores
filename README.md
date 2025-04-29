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

## 📜 Gramática da Linguagem PSALMS

```bnf
PROGRAMA           -> INICIO

INICIO             -> COMANDO INICIO | ε

COMANDO            -> DECLARACAO_VARIAVEL
                   | ATRIBUICAO
                   | CONDICAO
                   | ESTRUTURA_REPETICAO
                   | DEFINICAO_FUNCAO
                   | DEFINICAO_CLASSE
                   | ESCOLHA

-- Tipos e Identificadores
IDENTIFICADOR_VARIAVEL -> [a-zA-Z][a-zA-Z0-9_]* 
NUMERO_DECIMAL      -> [0-9]+ | [0-9]+\.[0-9]+
TIPO_VARIAVEL       -> "inteiro" | "flutuante" | "caractere" | "cadeia"

VALOR_VARIAVEL      -> IDENTIFICADOR_VARIAVEL | NUMERO_DECIMAL
EXPRESSAO           -> EXPRESSAO_ARITMETICA | EXPRESSAO_LOGICA | VALOR_VARIAVEL

-- Declaração e Atribuição de Variáveis
DECLARACAO_VARIAVEL -> TIPO_VARIAVEL IDENTIFICADOR_VARIAVEL "=" EXPRESSAO ";" 
ATRIBUICAO          -> IDENTIFICADOR_VARIAVEL "=" EXPRESSAO ";"

-- Expressões Aritméticas
EXPRESSAO_ARITMETICA -> NUMERO_DECIMAL | IDENTIFICADOR_VARIAVEL | EXPRESSAO_ARITMETICA OPERADOR_ARITMETICO EXPRESSAO_ARITMETICA
OPERADOR_ARITMETICO -> "+" | "-" | "*" | "/"

-- Condições (Controle de Fluxo)
CONDICAO            -> "se" "(" EXPRESSAO ")" BLOCO CONDICAO_FIM
CONDICAO_FIM        -> "senao" BLOCO | "senaose" "(" EXPRESSAO ")" BLOCO CONDICAO_FIM | ε

-- Estruturas de Repetição
ESTRUTURA_REPETICAO -> "enquanto" "(" EXPRESSAO ")" BLOCO
LOOP                -> "loop" "(" DECLARACAO_VARIAVEL ";" EXPRESSAO ";" ATRIBUICAO ")" BLOCO

-- Funções
DEFINICAO_FUNCAO    -> "deus" IDENTIFICADOR_VARIAVEL "(" PARAMETROS ")" BLOCO
PARAMETROS          -> TIPO_VARIAVEL IDENTIFICADOR_VARIAVEL | TIPO_VARIAVEL IDENTIFICADOR_VARIAVEL "," PARAMETROS | ε

-- Definição de Classe
DEFINICAO_CLASSE    -> "alma" IDENTIFICADOR_VARIAVEL BLOCO

-- Bloco de Código
BLOCO               -> "{" INICIO "}"

-- Expressões Lógicas
EXPRESSAO_LOGICA    -> EXPRESSAO_ARITMETICA OPERADOR_LOGICO EXPRESSAO_ARITMETICA | "nao" EXPRESSAO_LOGICA | VALOR_VARIAVEL
OPERADOR_LOGICO     -> "e" | "ou"

-- Controle de Fluxo: "parar", "continuar" e "amen"
COMANDO_PARAR       -> "parar" ";"
COMANDO_CONTINUAR   -> "continuar" ";"
COMANDO_RETORNO     -> "amen" EXPRESSAO ";"

-- Bloco de Escolha
ESCOLHA             -> "escolha" "(" EXPRESSAO ")" BLOCO ESCOLHA_FIM
ESCOLHA_FIM         -> "caso" EXPRESSAO BLOCO ESCOLHA_FIM | "padrao" BLOCO | ε
```
