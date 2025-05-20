PROGRAM PsalmsProgram;

VAR
  A: Integer;
  B: Integer;
  resultado: Integer;

PROCEDURE multiplicar(A: Integer; B: Integer);
BEGIN
  resultado := (A * B);
  WriteLn(resultado);
END;

BEGIN
  (* ATENÇÃO: Procedimento 'multiplicar' foi definido mas não utilizado *)
  ReadLn; (* Pausa antes de encerrar *)
END.
