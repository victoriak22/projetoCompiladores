PROGRAM PsalmsProgram;

USES
  SysUtils; // Para conversões String <-> número

VAR
  resultadoMultiplicacao: String;
  resultado: Double;

FUNCTION multiplicar(A: Variant; B: Variant): Variant;
BEGIN
  resultado := (A * B);
  multiplicar := resultado;
END;

BEGIN
  resultadoMultiplicacao := multiplicar;
  WriteLn('Resultado da multiplicação:');
  WriteLn(VarToStr(resultadoMultiplicacao));
END.
