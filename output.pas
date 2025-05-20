PROGRAM PsalmsProgram;

USES
  SysUtils; // Para conversões String <-> número

VAR
  resultadoMultiplicacao: String;
  teste: Integer;
  resultado: Double;

FUNCTION multiplicar(A: Variant; B: Variant): Variant;
BEGIN
  resultado := (A * B);
  teste := 12;
  multiplicar := resultado;
END;

BEGIN
  resultadoMultiplicacao := multiplicar;
  WriteLn('Resultado da multiplicação:');
  WriteLn(VarToStr(resultadoMultiplicacao));
END.
