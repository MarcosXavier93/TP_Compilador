/**
 * Compilador
 * PUC Minas - Compilers
 * Lecturer Alexei Machado
 *
 * @author Alan
 * @author Marcos Junio
 * @author Samuel Santos
 * @version 3.0
 */

import java.awt.Robot;
import java.io.BufferedReader;
import java.io.FileReader;

public class Parser {

    private LexicalAnalyzer lexical;
    private SymbolsTable symbolsTable;
    private Symbol symbol;
    private BufferedReader bufferedReader;
    private Buffer buffer;
    private Rotulo rotulo;
    private byte TYPE;
    private boolean IS_ARRAY;
    private String VALUE;
    Memoria memoria;
    private int endereco = memoria.contador;

    int F_end = 0;
    int T_end = 0;
    int Exps_end = 0;
    String Exps_tipo = "";
    int Exp_end = 0;
   // private String F1_tipo;

    public Parser(String fileName, String asmName) {
        try {
            memoria = new Memoria();
            rotulo = new Rotulo();
            bufferedReader = new BufferedReader(new FileReader(fileName));
            buffer = new Buffer(asmName);
            lexical = new LexicalAnalyzer(bufferedReader);
            symbolsTable = new SymbolsTable();
            symbol = lexical.FinitStateMachine();
            VALUE = "?";
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    void casaToken(byte expectedToken) {

        if (symbol.getToken() == expectedToken) {
            symbol = lexical.FinitStateMachine();
        } else {

            if (lexical.EOF) {
                ErrorMessages.Printf(ErrorMessages.FIM_ARQUIVO_NAO_ESPERADO, lexical.line, null);
            } else {
                ErrorMessages.Printf(ErrorMessages.TOKEN_NAO_ESPERADO, lexical.line, null);
            }
        }
    }

    /**
     * Procedimento S
     * S -> {D}*{C}*
     */
    public void S() {

        // D

        buffer.add("sseg SEGMENT STACK        ;início seg. pilha");
        buffer.add("byte 4000h DUP(?)         ;dimensiona pilha");
        buffer.add("sseg ENDS                 ;fim seg. pilha");
        buffer.add("dseg SEGMENT PUBLIC       ;início seg. Dados");
        buffer.add("byte 4000h DUP(?)         ;temporários");
        endereco = memoria.alocarTemp();

        while (symbol.getToken() == symbolsTable.VAR ||
                symbol.getToken() == symbolsTable.CONST) {
            D();
        }

        buffer.add("dseg ENDS ;fim seg. dados");
        buffer.add("cseg SEGMENT PUBLIC ;início seg. código");
        buffer.add("ASSUME CS:cseg, DS:dseg");
        buffer.add("strt:");
        buffer.add("mov AX, dseg");
        buffer.add("mov ds, AX");

        // C
        while (symbol.getToken() == symbolsTable.ID ||
                symbol.getToken() == symbolsTable.FOR ||
                symbol.getToken() == symbolsTable.IF ||
                symbol.getToken() == symbolsTable.DOT_COMMA ||
                symbol.getToken() == symbolsTable.READ_LN ||
                symbol.getToken() == symbolsTable.WRITE ||
                symbol.getToken() == symbolsTable.WRITE_LN) {
            C();
        }

        buffer.add("mov ah, 4Ch");
        buffer.add("int 21h");
        buffer.add("cseg ENDS ;fim seg. código");
        buffer.add("END strt ;fim programa");

        buffer.print();
    }

    public void D() {

        if (symbol.getToken() == symbolsTable.VAR) {
            casaToken(symbolsTable.VAR);
            do {
                X();
            } while (symbol.getToken() == symbolsTable.INTEGER ||
                    symbol.getToken() == symbolsTable.CHAR);
        } else if (symbol.getToken() == symbolsTable.CONST) {
            casaToken(symbolsTable.CONST);

            Symbol id1 = symbol;

            casaToken(symbolsTable.ID);

             /*Acao semantica*/
            if (symbol.getCategory() != Symbol.NO_CATEGORY)
                ErrorMessages.Printf(ErrorMessages.IDENTIFICADOR_JA_DECLARADO, lexical.line, null);
            else {
                symbol.setCategory(Symbol.CATEGORY_CONST);
            }

            casaToken(symbolsTable.EQUAL);
            if (symbol.getToken() == symbolsTable.MINUS) {
                casaToken(symbolsTable.MINUS);

                /*Acao semantica*/
                if (symbol.getType() != Symbol.TYPE_INTEGER)
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);

            }

            switch (symbol.getType()) {

                case Symbol.TYPE_INTEGER:
                    endereco = memoria.alocarInteiro();
                    buffer.add("sword " + symbol.getLexeme() + "             ; int " + id1);
                    break;

                case Symbol.TYPE_STRING:
                    endereco = memoria.alocarString();
                    buffer.add("byte " + symbol.getLexeme() + "            ; string " + id1);
                    break;
            }

            id1.setAddress(endereco);

            casaToken(symbolsTable.VALUE);


            casaToken(symbolsTable.DOT_COMMA);
        }

    }

    public void X() {

        if (symbol.getToken() == symbolsTable.INTEGER) {
            casaToken(symbolsTable.INTEGER);

            /*Acao semantica*/
            TYPE = Symbol.TYPE_INTEGER;

        } else {
            casaToken(symbolsTable.CHAR);

            /*Acao semantica*/
            TYPE = Symbol.TYPE_CARACTER;

        }

        /*Acao semantica*/
        if (symbol.getCategory() != Symbol.NO_CATEGORY)
            ErrorMessages.Printf(ErrorMessages.IDENTIFICADOR_JA_DECLARADO, lexical.line, null);
        else {
            symbol.setCategory(Symbol.CATEGORY_VAR);
        }

        Symbol id1 = symbol;

        casaToken(symbolsTable.ID);


        if (symbol.getToken() == symbolsTable.EQUAL ||
                symbol.getToken() == symbolsTable.OPEN_COLCH) {
            V();
        }

        switch (TYPE) {
            case Symbol.TYPE_INTEGER:
                if (IS_ARRAY) {
                    endereco = memoria.alocarString(Integer.parseInt(VALUE));
                    buffer.add("sword " + VALUE + " DUP(?)                 ; int[] " + id1.getLexeme());
                } else {
                    endereco = memoria.alocarInteiro();
                    buffer.add("sword " + VALUE + "                        ; int " + id1.getLexeme());
                }


                break;

            case Symbol.TYPE_CARACTER:
                if (IS_ARRAY) {
                    endereco = memoria.alocarByte();
                    buffer.add("byte " + VALUE + " DUP(?)                    ; char[] " + id1.getLexeme());
                }
                else {
                    endereco = memoria.alocarString(Integer.parseInt(VALUE));
                    buffer.add("byte " + VALUE + " DUP(?)                 ; char " + id1.getLexeme());
                }

                break;
        }

        id1.setAddress(endereco);

        while (symbol.getToken() == symbolsTable.COMMA) {
            casaToken(symbolsTable.COMMA);

            /*Acao semantica*/
            if (symbol.getCategory() != Symbol.NO_CATEGORY)
                ErrorMessages.Printf(ErrorMessages.IDENTIFICADOR_JA_DECLARADO, lexical.line, null);
            else {
                symbol.setCategory(Symbol.CATEGORY_VAR);
            }

            casaToken(symbolsTable.ID);

            Symbol id2 = symbol;

            if (symbol.getToken() == symbolsTable.ID ||
                    symbol.getToken() == symbolsTable.MINUS ||
                    symbol.getToken() == symbolsTable.OPEN_COLCH) {
                V();
            }

            switch (TYPE) {
                case Symbol.TYPE_INTEGER :
                    if (IS_ARRAY) {
                        endereco = memoria.alocarString(Integer.parseInt(VALUE));
                        buffer.add("sword " + VALUE + " DUP(?)                 ; int[] " + id2.getLexeme());
                    }
                    else{
                        endereco = memoria.alocarInteiro();
                        buffer.add("sword " + VALUE + "                        ; int " + id2.getLexeme());
                    }


                    break;

                case Symbol.TYPE_CARACTER :
                    if (IS_ARRAY) {
                        endereco = memoria.alocarByte();
                        buffer.add("byte " + VALUE + " DUP(?)                    ; char[] " + id2.getLexeme());
                    }
                    else {
                        endereco = memoria.alocarString(Integer.parseInt(VALUE));
                        buffer.add("byte " + VALUE + " DUP(?)                 ; char " + id2.getLexeme());
                    }

                    break;
            }

            id2.setAddress(endereco);

            /*Acao semantica*/
            id2.setType(TYPE);
        }
        casaToken(symbolsTable.DOT_COMMA);
    }

    public void V() {

        if (symbol.getToken() == symbolsTable.EQUAL) {
            casaToken(symbolsTable.EQUAL);
            if (symbol.getToken() == symbolsTable.MINUS) {
                casaToken(symbolsTable.MINUS);

                /*Acao semantica*/
                if (symbol.getType() != Symbol.TYPE_INTEGER)
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);

            }
            VALUE = symbol.getLexeme();
            casaToken(symbolsTable.VALUE);

            /*Acao semantica*/
            if (symbol.getType() != TYPE)
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);


        } else if (symbol.getToken() == symbolsTable.OPEN_COLCH) {
            casaToken(symbolsTable.OPEN_COLCH);

            /*Acao semantica*/
            if (symbol.getType() != Symbol.TYPE_INTEGER)
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);

            VALUE = symbol.getLexeme();
            casaToken(symbolsTable.VALUE);

            /* TODO: Verificar tamanho do vetor */

            casaToken(symbolsTable.CLOSE_COLCH);

            /*Acao semantica*/
            IS_ARRAY = true;
            ;
        }
    }

    public void C() {
        String Exp_tipo;
        Symbol temp;

        if (symbol.getToken() == symbolsTable.ID) {
            casaToken(symbolsTable.ID);
            casaToken(symbolsTable.EQUAL);
            EXP();
            casaToken(symbolsTable.DOT_COMMA);
        } else if (symbol.getToken() == symbolsTable.DOT_COMMA) {
            casaToken(symbolsTable.DOT_COMMA);
        } else if (symbol.getToken() == symbolsTable.FOR) {
            casaToken(symbolsTable.FOR);
            int end_ID = symbol.getAddress();
            casaToken(symbolsTable.ID);
            casaToken(symbolsTable.EQUAL);
            Exp_tipo = EXP();

            buffer.add("mov ax, DS:[" + Exp_end + "]");
            buffer.add("mov DS:[" + end_ID + "] , AX"  );

            String RotuloInicio = rotulo.novoRotulo();
            String RotuloFim = rotulo.novoRotulo();
            buffer.add(RotuloInicio + ":");

            //if (!Exp_tipo.equals("inteiro")) {
                //ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
            //}

            casaToken(symbolsTable.TO);
            Exp_tipo = EXP();

            buffer.add("mov BX, DS:[" + Exp_end + "]");
            buffer.add("cmp AX , BX");
            buffer.add("jg "+ RotuloFim);

            //if (!Exp_tipo.equals("logico")) {
                //ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
            //}

            if (symbol.getToken() == symbolsTable.STEP) {
                casaToken(symbolsTable.STEP);

                buffer.add("mov CX, DS["+ symbol.getAddress() +"]");
                /*Acao semantica*/
                if (symbol.getType() != Symbol.TYPE_INTEGER)
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);

                casaToken(symbolsTable.VALUE);
            }else {
                buffer.add("mov CX, 1");
            }

            buffer.add("add AX , CX");
            buffer.add("jmp "+ RotuloInicio);
            buffer.add(RotuloInicio + ":");

            casaToken(symbolsTable.DO);
            if (symbol.getToken() == symbolsTable.OPEN_KEYS) {
                casaToken(symbolsTable.OPEN_KEYS);
                while (symbol.getToken() == symbolsTable.ID || symbol.getToken() == symbolsTable.FOR
                        || symbol.getToken() == symbolsTable.IF || symbol.getToken() == symbolsTable.DOT_COMMA
                        || symbol.getToken() == symbolsTable.READ_LN || symbol.getToken() == symbolsTable.WRITE
                        || symbol.getToken() == symbolsTable.WRITE_LN) {
                    C();

                }
                casaToken(symbolsTable.CLOSE_KEYS);
            } else {
                C();
            }

            buffer.add("jmp " + RotuloInicio);
            buffer.add(RotuloFim + ":");

        } else if (symbol.getToken() == symbolsTable.IF) {
            casaToken(symbolsTable.IF);
            String rotuloFalso = rotulo.novoRotulo();
            String rotuloFim = rotulo.novoRotulo();

            Exp_tipo = EXP();

            /*Acao semantica*/
            if (!Exp_tipo.equals("logico"))
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);

            buffer.add("mov AX, DS["+ Exp_end +"]");
            buffer.add("cmp AX, 0");
            buffer.add("je " + rotuloFalso);

            casaToken(symbolsTable.THEN);
            if (symbol.getToken() == symbolsTable.OPEN_KEYS) {
                casaToken(symbolsTable.OPEN_KEYS);
                while (symbol.getToken() == symbolsTable.ID || symbol.getToken() == symbolsTable.FOR
                        || symbol.getToken() == symbolsTable.IF || symbol.getToken() == symbolsTable.DOT_COMMA
                        || symbol.getToken() == symbolsTable.READ_LN || symbol.getToken() == symbolsTable.WRITE
                        || symbol.getToken() == symbolsTable.WRITE_LN) {
                    C();
                }
                casaToken(symbolsTable.CLOSE_KEYS);
            } else {
                C();
            }
            if (symbol.getToken() == symbolsTable.ELSE) {
                casaToken(symbolsTable.ELSE);

                buffer.add("jmp " + rotuloFim);
                buffer.add(rotuloFalso + ":");

                if (symbol.getToken() == symbolsTable.OPEN_KEYS) {
                    casaToken(symbolsTable.OPEN_KEYS);
                    while (symbol.getToken() == symbolsTable.ID || symbol.getToken() == symbolsTable.FOR
                            || symbol.getToken() == symbolsTable.IF || symbol.getToken() == symbolsTable.DOT_COMMA
                            || symbol.getToken() == symbolsTable.READ_LN || symbol.getToken() == symbolsTable.WRITE
                            || symbol.getToken() == symbolsTable.WRITE_LN) {
                        C();
                    }
                    casaToken(symbolsTable.CLOSE_KEYS);
                } else {
                    C();
                }

                buffer.add(rotuloFim + ":");
            }

        } else if (symbol.getToken() == symbolsTable.READ_LN) {
            casaToken(symbolsTable.READ_LN);
            casaToken(symbolsTable.OPEN_PARENT);

            //if(symbol.getType() != symbol.TYPE_INTEGER && symbol.getType() != symbol.TYPE_STRING && symbol.getType() != symbol.TYPE_CARACTER)
              //  ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);

            temp = symbol;
            casaToken(symbolsTable.ID);
            casaToken(symbolsTable.CLOSE_PARENT);
            casaToken(symbolsTable.DOT_COMMA);

            int bufferEnd = memoria.alocarTempString();
            memoria.contTemp += 3;

            buffer.add("mov DX, " + bufferEnd);
            buffer.add("mov AL, 0FFh");
            buffer.add("mov DS:["+ bufferEnd +"], AL");
            buffer.add("mov AH, 0Ah");
            buffer.add("int 21h");

            buffer.add("mov AH, 02h");
            buffer.add("mov DL 0Dh");
            buffer.add("int 21h");
            buffer.add("mov DL, 0Ah");
            buffer.add("int 21h");

            buffer.add("mov DI, "+ (bufferEnd + 2) +";posicao do string");
            if(temp.getType() != symbol.TYPE_STRING) {
                buffer.add("mov AX, 0");
                buffer.add("mov CX, 10");
                buffer.add("mov DX, 1");
                buffer.add("mov BH, 0");
                buffer.add("mov BL, DS:[DI]");
                buffer.add("cmp BX, 2Dh");

                String rot = rotulo.novoRotulo();

                buffer.add("jne "+ rot);
                buffer.add("mov DX, -1");
                buffer.add("add DI, 1");
                buffer.add("mov BL, DS:[DI]");
                buffer.add(rot + ":");
                buffer.add("push DX");
                buffer.add("mov DX, 0");

                String rot1 = rotulo.novoRotulo();

                buffer.add(rot1 + ":");
                buffer.add("cmp BX, 0Dh");

                String rot2 = rotulo.novoRotulo();

                buffer.add("je " + rot2);
                buffer.add("imul CX");
                buffer.add("add BX, -48");
                buffer.add("add AX, BX");
                buffer.add("add DI, 1");
                buffer.add("mov BH, 0");
                buffer.add("mov BL, DS:[DI]");
                buffer.add("jmp " + rot1);
                buffer.add(rot2 + ":");
                buffer.add("pop CX");
                buffer.add("imul CX");

                buffer.add("mov DS:[" + temp.getAddress() + "], AX");
            } else {
                buffer.add("mov SI, " + temp.getAddress());

                String rotString = rotulo.novoRotulo();

                buffer.add(rotString + ":");
                buffer.add("mov AL, DS:[DI]");
                buffer.add("cmp AL, 0Dh ;verifica fim string");

                String rot2 = rotulo.novoRotulo();

                buffer.add("je " + rot2 + " ;salta se fim string");
                buffer.add("mov DS:[SI], AL ;próximo caractere");
                buffer.add("add DI, 1 ;incrementa base");
                buffer.add("add SI, 1");
                buffer.add("jmp " + rotString + " ;loop");
                buffer.add(rot2 + ":");
                buffer.add("mov AL, 024h ;fim de string");
                buffer.add("mov DS:[SI], AL ;grava '$'");
            }

        } else if (symbol.getToken() == symbolsTable.WRITE || symbol.getToken() == symbolsTable.WRITE_LN) {
            if (symbol.getToken() == symbolsTable.WRITE) {
                casaToken(symbolsTable.WRITE);
            } else {
                casaToken(symbolsTable.WRITE_LN);
            }
            casaToken(symbolsTable.OPEN_PARENT);
            EXP();
            while (symbol.getToken() == symbolsTable.COMMA) {
                casaToken(symbolsTable.COMMA);
                EXP();
            }
            casaToken(symbolsTable.CLOSE_PARENT);
            casaToken(symbolsTable.DOT_COMMA);
        }
    }

    public String EXP() {
        String exps_tipo = EXPS();
        String Exp_tipo = exps_tipo;
        Exp_end = Exps_end;
        int op = 0;

        if (symbol.getToken() == symbolsTable.LESS_THAN || symbol.getToken() == symbolsTable.MORE_THAN
                || symbol.getToken() == symbolsTable.LESS_EQUAL || symbol.getToken() == symbolsTable.MORE_EQUAL
                || symbol.getToken() == symbolsTable.EQUAL || symbol.getToken() == symbolsTable.DIFFERENT) {

            if (!exps_tipo.equals("inteiro") && (!exps_tipo.equals("string"))
                    && !exps_tipo.equals("caracter")) {
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
            } else {
                if (symbol.getToken() == symbolsTable.EQUAL) {
                    op = 5;
                    casaToken(symbolsTable.EQUAL);
                }
                if (exps_tipo.equals("string")) {
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
                } else {
                    if (symbol.getToken() == symbolsTable.MORE_THAN) {
                        op = 1;
                        casaToken(symbolsTable.MORE_THAN);
                    } else if (symbol.getToken() == symbolsTable.LESS_THAN) {
                        op = 2;
                        casaToken(symbolsTable.LESS_THAN);
                    } else if (symbol.getToken() == symbolsTable.MORE_EQUAL) {
                        op = 3;
                        casaToken(symbolsTable.MORE_EQUAL);
                    } else if (symbol.getToken() == symbolsTable.LESS_EQUAL) {
                        op = 4;
                        casaToken(symbolsTable.LESS_EQUAL);
                    } else if (symbol.getToken() == symbolsTable.DIFFERENT) {
                        op = 6;
                        casaToken(symbolsTable.DIFFERENT);
                    }
                }

            }

            String exps1_tipo = EXPS();
            if (!exps1_tipo.equals("inteiro") && !exps1_tipo.equals("caracter")) {
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
            }

            buffer.add("mov AX, DS: [" + Exp_end + "]");

            if (exps1_tipo.equals("logico")) {
                buffer.add("mov CX, AX");
                buffer.add("mov bl, DS:[" + Exps_end + "]");
                buffer.add("mov al, bl");
                buffer.add("mov ah, 0");
                buffer.add("mov BX, AX");
                buffer.add("mov AX, CX");
            } else {
                buffer.add("mov BX, DS:[" + Exps_end + "]");
            }

            buffer.add("cmp AX, BX");

            String rotuloVerdadeiro = rotulo.novoRotulo();

            switch (op) {
                case 1:
                    buffer.add("jg " + rotuloVerdadeiro);
                    break;
                case 2:
                    buffer.add("jl " + rotuloVerdadeiro);
                    break;
                case 3:
                    buffer.add("jge " + rotuloVerdadeiro);
                    break;
                case 4:
                    buffer.add("jle " + rotuloVerdadeiro);
                    break;
                case 5:
                    buffer.add("je " + rotuloVerdadeiro);
                    break;
                case 6:
                    buffer.add("jne " + rotuloVerdadeiro);
                    break;
            }

            buffer.add("mov AL, 0");

            String rotuloFalso = rotulo.novoRotulo();
            buffer.add("jmp " + rotuloFalso);
            buffer.add(rotuloVerdadeiro + ":");
            buffer.add("mov AL, 0FFh");
            buffer.add(rotuloFalso + ":");

            Exp_end = memoria.novoTemp();
            Exp_tipo = "logico";
            buffer.add("mov DS:[" + Exp_end + "], AL");

        }
        return Exp_tipo;
    }

    public String EXPS() {
        String Exps_tipo = "";
        boolean minus = false;
        if (symbol.getToken() == symbolsTable.PLUS || symbol.getToken() == symbolsTable.MINUS) {
            if (symbol.getToken() == symbolsTable.PLUS) {
                casaToken(symbolsTable.PLUS);
                minus = false;
            } else if (symbol.getToken() == symbolsTable.MINUS) {
                casaToken(symbolsTable.MINUS);
                minus = true;
            } else {
                minus = false;
            }
        }
        Exps_tipo = T();
        if (minus) {
            Exps_end = memoria.novoTemp();
            buffer.add("mov AL, DS:[" + T_end + "] ;");
            buffer.add("not AL");
            buffer.add("mov DS:[" + T_end + "], AL");
        }

        Exp_end = T_end;
        int op = 0;

        while (symbol.getToken() == symbolsTable.PLUS || symbol.getToken() == symbolsTable.MINUS
                || symbol.getToken() == symbolsTable.OR) {
            if (symbol.getToken() == symbolsTable.MINUS) {
                if (Exps_tipo.equals("string")) {
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
                }
                op = 1;
                casaToken(symbolsTable.MINUS);
            } else if (symbol.getToken() == symbolsTable.PLUS) {
                op = 2;
                casaToken(symbolsTable.PLUS);
            } else if (symbol.getToken() == symbolsTable.OR) {
                if (Exps_tipo.equals("string")) {
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
                }
                op = 3;
                casaToken(symbolsTable.OR);
            }
            int Tend = T_end;

            String T1_tipo = T();
            buffer.add("mov AX, DS:[" + Exps_end + "]");
            buffer.add("mov BX, DS:[" + T_end + "]");

            if (!Exps_tipo.equals(T1_tipo) && !(T1_tipo.equals("inteiro"))) {
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
            }

            switch (op) {
                case 1:
                    buffer.add("sub AX, BX ; minus");
                    break;
                case 2:
                    if (Exps_tipo.equals("string") && T1_tipo.equals("string")) {
                        int temp1 = memoria.novoTemp();
                        buffer.add("mov DS:[" + temp1 + "], AX");
                        buffer.add("add AX, BX");
                        buffer.add("mov AX, DS:[" + temp1 + "]");
                    } else {
                        buffer.add("add AX, BX ; plus");
                    }
                    break;
                case 3:
                    buffer.add("or AX, BX ; and");
                    break;
            }

            Exp_end = memoria.novoTemp();
            buffer.add("mov DS:[" + Exps_end + "], AX");
        }

        return Exps_tipo;
    }

    public String T() {
        String F_tipo = F();
        String F1_tipo = "";
        String T_tipo = F_tipo;
        T_end = F_end;
        int op = 0;

        while (symbol.getToken() == symbolsTable.MULTIPLY || symbol.getToken() == symbolsTable.DIVIDE
                || symbol.getToken() == symbolsTable.AND || symbol.getToken() == symbolsTable.PERCENT) {
            if (symbol.getToken() == symbolsTable.MULTIPLY) {
                if (F_tipo.equals("string") || F_tipo.equals("caracter")) {
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
                }
                op = 1;
                casaToken(symbolsTable.MULTIPLY);
            } else if (symbol.getToken() == symbolsTable.DIVIDE) {
                if (F_tipo.equals("string") || F_tipo.equals("caracter")) {
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
                }
                op = 2;
                casaToken(symbolsTable.DIVIDE);
            } else if (symbol.getToken() == symbolsTable.AND) {
                if (F_tipo.equals("string") || F_tipo.equals("caracter")) {
                    ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
                }
                op = 3;
                casaToken(symbolsTable.AND);
            }
           // System.out.println(F_tipo == null);
            F1_tipo = F();
            System.out.println("aqui");
            buffer.add("mov al, DS:[" + T_end + "]");
            buffer.add("mov bx, DS:[" + F_end + "]");

            if (op == 2) {
                buffer.add("cwd");
                buffer.add("mov CX, AX ; salvar o que tinha em al");
                buffer.add("mov AX, DS:[" + F_end + "] ; mover F1.end para al");
                buffer.add("cwd");
                buffer.add("mov BX, AX ; voltar F1.end para BX");
                buffer.add("mov AX, CX ;voltar valor anterior de AX");
            }
        }

        switch (op) {
            case 1:
                buffer.add("imul BX ; multiplicacao");
                break;
            case 2:
                buffer.add("idiv BX ; divisao");
                buffer.add("sub AX, 256; divisao");
                break;
            case 3:
                buffer.add("and AX, BX ; and");
                break;
        }

        T_end = memoria.novoTemp();
        buffer.add("mov DS:[" + T_end + "], AX");

        //System.out.println(F1_tipo == null);
        if(!T_tipo.equals(F1_tipo) && !((T_tipo.equals("inteiro") || (F1_tipo.equals("inteiro"))))) {
            ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
        }

        if((T_tipo.equals("inteiro") && F1_tipo.equals("inteiro"))){
            T_tipo = "inteiro";
        }

        return T_tipo;
    }

    public String F() {
        String F_tipo = "";
        if (symbol.getToken() == symbolsTable.ID) {
            //if (symbol.getCategory() == Symbol.NO_CATEGORY) {
                //ErrorMessages.Printf(ErrorMessages.IDENTIFICADOR_JA_DECLARADO, lexical.line, symbol.getLexeme());
            //} else {
                if (symbol.getType() == Symbol.TYPE_INTEGER) {
                    F_tipo = "inteiro";
                } else if (symbol.getType() == Symbol.TYPE_STRING) {
                    F_tipo = "string";
                } else if (symbol.getType() == Symbol.TYPE_CARACTER) {
                    F_tipo = "caracter";
                } else if (symbol.getType() == Symbol.TYPE_ARRAY) {
                    F_tipo = "array";
                }
            //}
            F_end = symbol.getAddress();
            casaToken(symbolsTable.ID);

            if (symbol.getToken() == symbolsTable.OPEN_COLCH) {
                casaToken(symbolsTable.OPEN_COLCH);
                EXPS();
                casaToken(symbolsTable.CLOSE_COLCH);
            }
            // TODO: 29/11/17 verificar e gerar array
        } else if (symbol.getToken() == symbolsTable.VALUE) {
            if (symbol.getType() == Symbol.TYPE_STRING) {
                buffer.add("dseg SEGMENT PUBLIC");
                // buffer.add("byte " + symbol.getLexema().substring(0, symbol.getLexema().length() - 1) + "$" + symbol.getLexema().charAt(symbol.getLexema().length() - 1));
                buffer.add("dseg ENDS");
                F_end = memoria.contador;
                memoria.alocarString(symbol.getLexeme().length() - 1);
            } else {
                String lex = symbol.getLexeme();
                if (symbol.getLexeme().toLowerCase().equals("true")) {
                    lex = "OFFh";
                } else if (symbol.getLexeme().toLowerCase().equals("false")) {
                    lex = "0h";
                }
                F_end = memoria.novoTemp();
                buffer.add("mov ax, " + lex + " ; const " + symbol.getLexeme());
                buffer.add("mov DS:[" + F_end + "], al");

                if (symbol.getType() == Symbol.TYPE_INTEGER) {
                    memoria.alocarInteiro();
                } else if (symbol.getType() == Symbol.TYPE_LOGICAL) {
                    memoria.alocarLogico();
                }
            }
            buffer.add("; " + symbol.getLexeme() + " em " + F_end);
            casaToken(symbolsTable.VALUE);
        } else if (symbol.getToken() == symbolsTable.NOT) {
            if (F_tipo.equals("string") || F_tipo.equals("logico")) {
                ErrorMessages.Printf(ErrorMessages.TIPOS_INCOMPATIVEIS, lexical.line, null);
            }
            casaToken(symbolsTable.NOT);
            F_tipo = "inteiro";
            int Fend = F_end;
            F();
            Fend = memoria.novoTemp();
            buffer.add("mov AL, DS:[" + F_end + "] ;");
            buffer.add("not AL");
            buffer.add("mov DS:[" + Fend + "], AL");
            F_end = Fend;
        } else if (symbol.getToken() == symbolsTable.OPEN_PARENT) {
            casaToken(symbolsTable.OPEN_PARENT);
            F_tipo = EXP();
            F_end = Exp_end;
            casaToken(symbolsTable.CLOSE_PARENT);
        }
        return F_tipo;
    }
}
