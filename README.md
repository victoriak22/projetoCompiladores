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
EXEMPLO 1 — LOOP COM CONDICIONAL:

minhaAlma -> lista {
	loop(i -> 0; i < 5; i -> i + 1) {
		se(i % 2 == 0):
			="Número par: " + i
		senao:
			="Número ímpar: " + i
	}
}


EXEMPLO 2 — FUNÇÃO COM RETORNO:

somaDivina -> A, B {
	:resultado -> A + B
	amen :resultado
}

resultadoFinal -> somaDivina(5, 7)
="Resultado da soma: " + resultadoFinal


EXEMPLO 3 — ESCOLHA (SWITCH/CASE):

verboCelestial -> comando {
	escolha(comando) {
		caso "orar":
			="Você escolheu orar."
			parar
		caso "louvar":
			="Você escolheu louvar."
			parar
		padrao:
			="Comando desconhecido."
	}
}


EXEMPLO 4 — TRATAMENTO DE ERROS:

divisaoCelestial -> A, B {
	tente {
		se(B == 0):
			lancarErro -> "Divisão pelas trevas"
		:resultado -> A / B
		amen :resultado
	}
	capturar(e):
		="Erro: " + e
}


EXEMPLO 5 — CLASSE SIMPLES:

publico alma Pessoa {
	:nome -> ""
	:idade -> 0

	deus construtor -> nomeParam, idadeParam {
		este.nome -> nomeParam
		este.idade -> idadeParam
	}

	deus falar -> {
		="Olá, meu nome é " + este.nome
	}
}
```

---

## 📜 Gramática da Linguagem PSALMS

```bnf
ListaComandos → Comando ListaComandos | ε

Comando → Declaracao 
        | Atribuicao 
        | EstruturaCondicional 
        | EstruturaLoop
        | EstruturaEscolha 
        | TratamentoErros 
        | ChamadaFuncao 
        | Comentario

Comentario → "--" texto

Declaracao → deus identificador -> Parametros { ListaComandos }

Parametros → identificador 
           | identificador , Parametros 
           | ε

Atribuicao → :identificador -> Expressao
           | este.identificador -> Expressao

Expressao → Valor 
          | Expressao operador Expressao 
          | ChamadaFuncao

Valor → literal_numerico 
      | literal_texto 
      | identificador 
      | luz 
      | trevas 
      | nulo

ChamadaFuncao → identificador ( Parametros )

EstruturaCondicional → se (Expressao): ListaComandos
                     | senaose (Expressao): ListaComandos
                     | senao: ListaComandos

EstruturaLoop → loop (Atribuicao ; Expressao ; Atribuicao) { ListaComandos }
              | enquanto (Expressao) { ListaComandos }

EstruturaEscolha → escolha (Expressao) {
                     Casos padrao
                   }

Casos → caso Valor : ListaComandos Casos 
      | ε

padrao → padrao : ListaComandos

TratamentoErros → tente { ListaComandos } capturar (identificador): ListaComandos
```
