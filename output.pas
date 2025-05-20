PROGRAM PsalmsProgram;

USES
  SysUtils; // Para conversões String <-> número

VAR
  idade: String;
  contador: Double;
  nome: String;

BEGIN
  nome := '';
  idade := 0;
  WriteLn('Digite seu nome:');
  ReadLn(nome);
  WriteLn('Digite sua idade:');
  ReadLn(idade);
  WriteLn('Olá!');
  WriteLn('Seu nome é:');
  WriteLn(nome);
  WriteLn('Sua idade é:');
  WriteLn(idade);
  WriteLn('Anos');
  IF (idade >= 18) THEN
  BEGIN
    WriteLn('Você é maior de idade.');
  END
  ELSE
  BEGIN
    WriteLn('Você é menor de idade.');
  END;
  WriteLn('Contagem até 5:');
  contador := 1;
  WHILE (contador <= 5) DO
  BEGIN
    WriteLn(FloatToStr(contador));
    contador := (contador + 1);
  END;
  WriteLn('Fim do programa!');
END.
