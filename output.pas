PROGRAM PsalmsProgram;

VAR
  resultadoMultiplicacao: Integer;
  resultado: Integer;

FUNCTION multiplicar(A: Integer; B: Integer): Integer;
VAR
  resultado: Integer;
BEGIN
  resultado := (A * B);
  Result := resultado;
END;

BEGIN
  resultadoMultiplicacao := multiplicar(3, 4);
('Resultado da multiplicação: ' + resultadoMultiplicacao)END.
