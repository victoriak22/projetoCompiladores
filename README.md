# **Bem-vindo ao Compilador da Linguagem PSALMS! 🇧🇷**

O **Compilador PSALMS** é uma ferramenta que traduz código-fonte de uma linguagem de programação simplificada para uma versão que utiliza palavras em português, tornando o processo de desenvolvimento mais acessível e fluído para falantes de português. 🌍💻

A linguagem **PSALMS** foi projetada com uma forte influência de palavras que fazem referência ao cristianismo, tornando o código não apenas mais intuitivo, mas também com um toque simbólico e espiritual. 🙏

---

### 💡 **Transformações de Palavras Reservadas (Java para PSMALS)**

Aqui estão as transformações de palavras reservadas da linguagem **Java** para **PSMALS**:

| **Palavra em Java**    | **Palavra em PSMALS (Português)** |
|------------------------|------------------------------------|
| `if`                   | `se`                               |
| `else`                 | `senao`                            |
| `else if`              | `senaose`                          |
| `for`                  | `loop`                             |
| `while`                | `enquanto`                         |
| `break`                | `parar`                            |
| `continue`             | `continuar`                        |
| `return`               | `amen`                             |
| `try`                  | `tente`                            |
| `catch`                | `capturar`                         |
| `class`                | `alma`                             |
| `public`               | `publico`                          |
| `private`              | `privado`                          |
| `void`                 | `vazio`                            |
| `int`                  | `inteiro`                          |
| `float`                | `flutuante`                        |
| `char`                 | `caractere`                        |
| `String`               | `cadeia`                           |
| `true`                 | `luz`                              |
| `false`                | `trevas`                           |
| `null`                 | `nulo`                             |
| `new`                  | `gen`                              |
| `this`                 | `este`                             |
| `super`                | `superior`                         |
| `instanceof`           | `como`                             |
| `switch`               | `escolha`                          |
| `case`                 | `caso`                             |
| `default`              | `padrao`                           |

---

### 🛠 **Como Funciona o Compilador PSALMS?**

O **compilador PSALMS** realiza um processo de tradução do código-fonte em várias etapas, passando pelas fases de análise léxica, sintática e semântica. Cada uma dessas fases é crucial para garantir que o código seja corretamente interpretado e transformado na versão desejada.

1. **Análise Léxica**: 
   - O compilador começa com a análise léxica, onde o código-fonte é lido e dividido em **tokens**. Um token é a unidade básica de informação que o compilador utiliza, como palavras reservadas, identificadores, operadores, números, etc.
   - Durante essa fase, o compilador identifica as **palavras reservadas** (como `if`, `else`, `while`) e as converte para a versão em português correspondente (como `se`, `senao`, `enquanto`). Cada palavra reservada é transformada em um **token de palavra reservada** que indica tanto o tipo (como `PALAVRA_RESERVADA`) quanto o valor da palavra.
   - O compilador também identifica outros elementos, como variáveis e números, para construir os tokens adequados.

2. **Análise Sintática**:
   - Após a análise léxica, o código é analisado sintaticamente, ou seja, o compilador verifica a **estrutura** do código-fonte, garantindo que ele siga as regras da gramática da linguagem PSALMS.
   - Durante essa fase, o compilador cria uma **árvore sintática** (AST - Abstract Syntax Tree), que representa a hierarquia e a organização do código.
   - Se o código violar as regras sintáticas da linguagem (como a falta de parênteses ou chaves), o compilador gera erros de sintaxe indicando o problema.

3. **Análise Semântica**:
   - A última fase é a análise semântica, onde o compilador verifica a **consistência lógica** do código, garantindo que as instruções sejam válidas de acordo com o significado da linguagem.
   - O compilador valida o uso das variáveis (por exemplo, se uma variável foi declarada antes de ser utilizada), tipos de dados (como garantir que a variável `inteiro` seja usada com valores inteiros) e o comportamento do código em termos de operações válidas (como divisões por zero ou chamadas de funções com argumentos errados).
   - Caso detecte inconsistências semânticas, como operações inválidas ou uso inadequado de variáveis, o compilador gera erros semânticos.

Essas três etapas — **lexical**, **sintática** e **semântica** — garantem que o código-fonte seja corretamente processado, transformado e executado, permitindo que a linguagem PSALMS seja interpretada de maneira precisa e eficiente.

---

Esses processos garantem que o código-fonte em **PSALMS** seja não apenas convertido para o português, mas também validado em termos de sua estrutura e significado, resultando em um compilador robusto e confiável.

---

### 💡 **Exemplos de Código**

#### **Exemplo de código com `se` e `senao` (substituindo `if` e `else`):**

```plaintext
deus verificarNumero(inteiro x) {
    se (x > 10) {
        amen "Maior que 10!";
    } senao {
        amen "Menor ou igual a 10!";
    }
}
