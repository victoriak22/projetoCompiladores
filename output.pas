PROGRAM PsalmsProgram;

USES
  SysUtils; // Para conversões String <-> número

VAR
  texto: String;
  inteiro: Integer;
  booleano: Boolean;
  decimal: Double;

BEGIN
  inteiro := 42;
  decimal := 3.14;
  booleano := True;
  texto := 'Olá mundo!';
  WriteLn('''Valor inteiro: ''', IntToStr(inteiro));
  WriteLn('''Valor decimal: ''', FloatToStr(decimal));
  WriteLn('''Valor booleano: ''', BoolToStr(booleano, 'luz', 'trevas'));
  WriteLn('''Valor texto: ''', texto);
END.
