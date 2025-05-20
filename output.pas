PROGRAM PsalmsProgram;

VAR
  n1: Integer;
  n2: Integer;

BEGIN
  n1 := 0;
  n2 := 0;
  WriteLn('Digite o primeiro numero');
  ReadLn(n1);
  WriteLn('Digite o segundo numero');
  ReadLn(n2);
  WriteLn('Voce digitou os numeros:');
  WriteLn(n1);
  WriteLn(n2);
  IF (n1 > n2) THEN
  BEGIN
    WriteLn('O primeiro numero e maior que o segundo');
  END
  ELSE
  BEGIN
    IF (n1 < n2) THEN
    BEGIN
      WriteLn('O segundo numero e maior que o primeiro');
    END
    ELSE
    BEGIN
      WriteLn('Os dois numeros sao iguais');
    END;
  END;
  WriteLn('Fim do programa');
END.
