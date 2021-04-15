/**
 * Compiler for the L (made up) language
 * PUC Minas - Compilers
 * Lecturer Alexei Machado
 *
 * @author Geyson Inacio
 * @author Izabela Borges
 * @author Taina Viriato
 * @version 1.0
*/

import java.io.BufferedReader;

public class LexicalAnalyzer {

    private BufferedReader _bufferedReader;
    public long line;
    public boolean EOF;
    private char lastChar;
    private String lexema;
    private boolean canReturn;
    private boolean compileError;
    private Symbol _symbol;
    private SymbolsTable symbolsTable;
    private byte TYPE;

    public LexicalAnalyzer(BufferedReader bufferedReader) {
        symbolsTable = new SymbolsTable();
        _bufferedReader = bufferedReader;
        line = 1;
        EOF = false;
        canReturn = false;
        compileError = false;
    }
  
    private char readChar(){

        try {
            if (canReturn){
                canReturn = false;      
            }
            else {
                lastChar = (char)_bufferedReader.read();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    
        return lastChar;
    }

    public Symbol FinitStateMachine(){
        lexema = "";
        int state = 0;
        int finalState = 13;
        while (state != finalState) {
            switch (state) {
                case 0 : state = state0(); break;
                case 1 : state = state1(); break;
                case 2 : state = state2(); break;
                case 3 : state = state3(); break;
                case 4 : state = state4(); break;
                case 5 : state = state5(); break;
                case 6 : state = state6(); break;
                case 7 : state = state7(); break;
                case 8 : state = state8(); break;
                case 9 : state = state9(); break;
                case 10 : state = state10(); break;
                case 11 : state = state11(); break;
                case 12 : state = state12(); break;
                case 13 : state = 13; break;
            }
        }
        
        if (compileError)
            System.exit(0);
        
        if (!EOF) {
            if (symbolsTable.searchLexeme(lexema) != null) {
                _symbol = symbolsTable.searchLexeme(lexema);
            } else {
                if (lexema.charAt(0) == '"' || Letter.IsDigit((lexema.charAt(0))))
                    _symbol = symbolsTable.insertConst(lexema, TYPE);
                else{

                    _symbol = symbolsTable.insertID(lexema, TYPE);
                }
            }
        }
        else {
            _symbol = new Symbol((byte)-1, "");
        } 

        return _symbol;
    }

    private int state0(){
        lexema = "";
        char symbol = readChar();
        if (Letter.IsLetter(symbol)) {
            lexema += symbol;
            return 1;
        }
        if (Letter.IsDigit(symbol)) { 
            lexema += symbol;      
            if (symbol == '0')
                return 3;
            return 6;
        }
        if (Letter.IsSpecialCharacter(symbol)) {
            lexema += symbol;           
            if (symbol == Letter.DOT || symbol == Letter.UNDERSCORE)
                return 2;
            if (symbol == Letter.MORE_THAN)
                return 8;
            if (symbol == Letter.LESS_THAN)
                return 9;
            if (symbol == Letter.DIVIDE){
                return 10;
            } 
            
            return 13;
        } 
        
        if (symbol == Letter.QUOTATION_MARK){
            lexema += symbol;
            TYPE = Symbol.TYPE_STRING;
            return 7; 
        }

        if (symbol == Letter.LINE_FEED || symbol == Letter.VERTICAL_TABULATION){
            line++;
            return 0;
        }

        if (Letter.IsControlCharacter(symbol))   
            return 0;
              
        if (symbol == Letter.EOF){
            EOF = true;
            return 13;
        }

        return error(symbol);
    }

    public int state1(){
        char symbol = readChar();
        if (Letter.IsDigit(symbol) || Letter.IsLetter(symbol) || symbol == Letter.UNDERSCORE || symbol == Letter.DOT){
            lexema += symbol;
            return 1;
        }
        if (Letter.IsValidChar(symbol)){ 
            canReturn = true;
            return  13;
        }
        return error(symbol);
    }

    public int state2(){
        char symbol = readChar();     
        if (symbol == Letter.DOT || symbol == Letter.UNDERSCORE){
            lexema += symbol;
            return 2;
        }                  
        if (Letter.IsDigit(symbol) || Letter.IsLetter(symbol)){
            lexema += symbol;
            return 1;
        }
        return error(symbol);
    }

    public int state3(){
        char symbol = readChar();   
        if (symbol == 'x') {
            lexema += symbol;
            TYPE = Symbol.TYPE_CARACTER;
            return 4;
        }
        if (Letter.IsDigit(symbol)) {
            lexema += symbol;
            return 6;
        }
        if (Letter.IsValidChar(symbol)) {
            canReturn = true;
            return  13;
        }
        return error(symbol);
    }

    public int state4(){
        char symbol = readChar();
        if (Letter.IsDigit(symbol) || Letter.IsHexa(symbol)) {
            lexema += symbol;
            return 5;
        }
        return error(symbol);
    }

    public int state5(){
        char symbol = readChar();
        if (Letter.IsDigit(symbol) || Letter.IsHexa(symbol)) {
            lexema += symbol;
            return  13;
        }
        return error(symbol);
    }
    
    public int state6(){
        char symbol = readChar();
        TYPE = Symbol.TYPE_INTEGER;
        if (Letter.IsDigit(symbol)) {
            lexema += symbol;
            return 6;
        }
        if (Letter.IsValidChar(symbol)) {
            canReturn = true;
            return  13;
        }
        return error(symbol);
    }

    public int state7(){
        char symbol = readChar();
        if (Letter.IsDigit(symbol) || Letter.IsLetter(symbol) || Letter.IsSpecialCharacter(symbol) || symbol == Letter.SPACE || symbol == Letter.VERTICAL_TABULATION) {
            lexema += symbol;
            return 7;
        }
        if (symbol == Letter.QUOTATION_MARK) {
            lexema += symbol;
            return  13;
        }
        return error(symbol);
    }

    public int state8(){
        char symbol = readChar();
        if (symbol == Letter.EQUAL) {
            lexema += symbol;
            return  13;
        }
        if (Letter.IsValidChar(symbol)) {
            canReturn = true;
            return  13;
        }
        return error(symbol);
    }
    
    public int state9(){
        char symbol = readChar();
        if (symbol == Letter.EQUAL) {
            lexema += symbol;
            return  13;
        }
        if (symbol == Letter.MORE_THAN) {
            lexema += symbol;
            return  13;
        }
        if (Letter.IsValidChar(symbol)) {
            canReturn = true;
            return  13;
        }
        return error(symbol);
    }

    public int state10(){
        char symbol = readChar();
        if (symbol == Letter.MULTIPLY)
            return 11;
        if (Letter.IsValidChar(symbol)) {
            canReturn = true;
            return  13;
        }
        return error(symbol);
    }

    public int state11(){
        char symbol = readChar();
        if (symbol == Letter.LINE_FEED || symbol == Letter.VERTICAL_TABULATION)
            line++;
        if (symbol == Letter.MULTIPLY)
            return 12;
        if (Letter.IsValidChar(symbol) && symbol != Letter.DIVIDE)
            return 11;
        return error(symbol);
    }

    public int state12() {
        char symbol = readChar();
        if (symbol == Letter.DIVIDE) {
            return 0;
        }
        if (symbol == Letter.MULTIPLY) {
            return 12;
        }
        if (Letter.IsValidChar(symbol)) {
            return 11;
        }
        return error(symbol);
    }

    public int error(char symbol){
        compileError = true;
        if (symbol == Letter.EOF){
            ErrorMessages.Printf(ErrorMessages.FIM_ARQUIVO_NAO_ESPERADO, line, null);
            EOF = true;
            return 13;
        }
       
        if (Letter.IsValidChar(symbol))
            ErrorMessages.Printf(ErrorMessages.LEXEMA_NAO_IDENTIFICADO, line, symbol + "");
        else
            ErrorMessages.Printf(ErrorMessages.CARACTERE_INVALIDO, line,  null);
        
        return 13;
    }
}