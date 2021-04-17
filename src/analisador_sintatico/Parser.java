package analisador_sintatico;

import analisador_lexico.Tag;
import analisador_lexico.Token;
import analisador_semantico.VerificadorSemantico;  // MUDANÇA
import java.util.ArrayList;
import java.util.Hashtable;

public class Parser {
    private Token tok;
    private int tag;
    private int i;
    private int line;
    private ArrayList<Token> tokens = new ArrayList<Token> ();
    private VerificadorSemantico vs;  // MUDANÇA
    private int nID;
    
    public Parser(ArrayList<Token> tokens){
        this.tokens=tokens;
        i=0;
        tok=tokens.get(i);
        tag=tok.tag;
        line=tok.line;
        vs = new VerificadorSemantico(); // MUDANÇA
    }

    // MUDANÇA
    public Hashtable<String, Integer> getTS() {
        return vs.getTS();
    }
    // -

    public void init(){
        program();
    };

    private void advance(){
        i++;
        tok=tokens.get(i);
        tag=tok.tag;
        line=tok.line;
    }

    private void error(String funcaoDoErro){
        if(tag==Tag.EOF) {
            if(line==-4)
                System.out.println("Arquivo de entrada vazio");
            return;
        }
        System.out.print(funcaoDoErro+";"+" Error(" + line + "): Token não esperado:"); //debug
        tok.imprimeToken(tok);
        while(tag!=Tag.EOF)
            advance();
    } 

    private void eat(int t){
        if(tag==t){
            //Checa se a tag e um tipo basico
            if(tag == Tag.INT || tag == Tag.STR || tag == Tag.REAL){
                vs.setCurType(tag);
            }
            //Caso ; deve resetar o resultado esperado de uma expressao
            if(tag == Tag.PV){
                vs.resetResultExprType();
            }
            System.out.print("Token Consumido("+line+"): ");
            tok.imprimeToken(tok);
            advance();
        }
        else { error("Error in: eat"); }
    }

    private void program(){
        switch(tag) {
            //G:: program ::= init [decl-list] begin stmt-list stop
            case Tag.INIT:
                eat(Tag.INIT); declList();  
                if (tag == Tag.BEGIN) {
                    eat(Tag.BEGIN); stmtList();
                    if(tag == Tag.STOP){
                        eat(Tag.STOP);
                    }else{
                        System.out.println("Fim de arquivo inesperado.");
                    }
                } else if (tag == Tag.EOF) {
                    System.out.println("Fim de arquivo inesperado.");
                }
                vs.imprimirTS();  // MUDANÇA
                break;
            default:
                error("Error in: program");
        }
    }

    private void declList(){
        //G:: decl-list ::= decl ";" { decl ";"}
        decl(); 
        switch(tag) {
            case Tag.PV:
                eat(Tag.PV);
                if(tag == Tag.BEGIN){ 
                    break;
                }else{
                    declList();
                }
                break; 
            default:
                error("Error in: declList");
        }   
    }

    private void decl(){      
        identList();   
        switch(tag) {
            //G:: decl ::= ident-list is type
            case Tag.IS:
                eat(Tag.IS); type();
                break;
            case Tag.VRG:
                eat(Tag.VRG); decl();
                break;
            default:
                error("Error in: decl");
        }
    }

    private void identList(){
        switch(tag) {
            //G:: ident-list ::= identifier {"," identifier}
            case Tag.ID:
                vs.putTS(tok, line); eat(Tag.ID);     // MUDANÇA
                break;
            case Tag.VRG:
                eat(Tag.VRG); vs.putTS(tok, line); eat(Tag.ID);  // MUDANÇA
                break;     
            default:
                error("Error in: identList");
        }
    }

    private void type(){
        switch(tag) {
            //G:: type ::= int
            case Tag.INT:
                eat(Tag.INT);
                break;
            //G:: type ::= string
            case Tag.STR:
                eat(Tag.STR);
                break;
            //G:: type ::= real
            case Tag.REAL:
                eat(Tag.REAL); 
                break;        
            default:
                error("Error in: type");
        }
    }

    private void stmtList(){
        switch(tag) {
            //G:: stmt-list ::= stmt ";" { stmt ";" }
            case Tag.IF:
                stmt(); stmtList();
                break;
            case Tag.ID:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                stmt(); eat(Tag.PV); stmtList();
                break;
            case Tag.WHILE:
                break;
            case Tag.STOP:
                break;    
            case Tag.END:
                break;
            default:
                error("Error in: stmtList");
        }
    }

    private void stmt(){
        switch(tag) {
            //G:: stmt ::= assign-stmt
            case Tag.ID:
                assignStmt();
                break;
            //G:: stmt ::= if-stmt
            case Tag.IF:
                ifStmt();
                break;
            //G:: stmt ::= while-stmt
            case Tag.DO:
                doStmt();
                break;
            //G:: stmt ::= read-stmt 
            case Tag.READ:
                readStmt();
                break;
            //G:: stmt ::= write-stmt 
            case Tag.WRITE:
                writeStmt();
                break;
            default:
                error("Error in: stmt");
        }
    }

    private void assignStmt(){
        switch(tag) {
            //G:: assign-stmt ::= identifier ":=" simple_expr
            case Tag.ID:
                vs.setCurAssignStmtType(tok, line); eat(Tag.ID); eat(Tag.PPV); simpleExpr(); // MUDANÇA
                break;
            default:
                error("Error in: assignStmt");
        }
    }

    private void ifStmt(){
        switch(tag) {
            //G:: if-stmt ::= if "(" condition ")" begin stmt-list end else begin stmt-list end
            case Tag.IF:
                eat(Tag.IF); vs.setStartingCondition(); eat(Tag.AP);  condition();  eat(Tag.FP); vs.endStartingCondition(); eat(Tag.BEGIN); stmtList(); eat(Tag.END);
 
            case Tag.ELSE:
                eat(Tag.ELSE); eat(Tag.BEGIN); stmtList(); eat(Tag.END);
                break;
            default:
                error("Error in: ifStmt");
        }
    }

    private void doStmt(){
        switch(tag) {
            //G:: do-stmt ::= do stmt-list do-suffix.
            case Tag.DO:
                eat(Tag.DO); stmtList(); doSufix();
                break;
            default:
                error("Error in: doStmt");
        }
    }

    private void doSufix(){
        switch(tag) {
            //G:: stmt-sufix ::= while "(" condition ")"
            case Tag.WHILE:
                eat(Tag.WHILE); eat(Tag.AP); vs.setStartingCondition(); condition(); vs.endStartingCondition(); eat(Tag.FP);
                break;
            default:
                error("Error in: doSufix");
        }
    }

    private void readStmt(){
        switch(tag) {
            //G:: read-stmt ::= read "(" identifier ")"
            case Tag.READ:
                eat(Tag.READ); eat(Tag.AP); eat(Tag.ID); eat(Tag.FP);
                break;
            default:
                error("Error in: redStmt");
        }
    }

    private void writeStmt(){
        switch(tag) {
            //G:: write-stmt ::= write "(" writable ")"
            case Tag.WRITE:
                eat(Tag.WRITE); eat(Tag.AP); writable(); eat(Tag.FP);
                break;
            default:
                error("Error in: writeStmt");
        }
    }

    private void writable() {
        simpleExpr();
    }
    
    private void condition(){
        switch(tag) {
            //G:: expression ::= simple-expr | simple-expr relop simple-expr
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
            case Tag.NOT:
            case Tag.MIN:
                simpleExpr();
                switch(tag){
                    case Tag.GT:
                    case Tag.LT:
                    case Tag.GE:
                    case Tag.LE:
                    case Tag.NE:
                    case Tag.EQ:
                        relop(); simpleExpr(); 
                        break;
                    case Tag.FP:
                        break;
                    default:
                        error("Error in: condition, 'simple-expr relop simple-expr'");
                }
                break;
            default:
                error("Error in: condition, 'simple-expr'");
        }
    }

    private void simpleExpr(){
        switch(tag) {
            //G:: simple-expr ::= term | simple-expr addop term
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
            case Tag.NOT:
            case Tag.MIN:
                term();
                switch(tag){
                    case Tag.MIN:    
                    case Tag.SUM:
                        addop(); term();
                        break;
                    case Tag.PV:
                    case Tag.FP:
                    case Tag.GT:
                    case Tag.LT:
                    case Tag.GE:
                    case Tag.LE:
                    case Tag.NE:
                    case Tag.EQ:
                        break;
                    default:
                        error("Error in: simpleExpr, 'simple-expr addop term'");
                    }
                break; 
            default:
                error("Error in: simpleExpr, 'term'");   
        }
    }

    private void term(){
        switch(tag) {
            //G:: term ::= factor-a | term mulop factor-a
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
            case Tag.NOT:
            case Tag.MIN:
                factorA(); term();
                break;   
            case Tag.PV:
            case Tag.FP:
            case Tag.SUM:
            case Tag.GT:
                break;
            case Tag.MUL:
            case Tag.DIV:
            case Tag.AND:
                mulop(); factorA();
                break;
            default:
                error("Error in: termA");
        }
    }


    private void factorA(){
        switch(tag) {
            //G:: factor-a ::= factor 
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
                factor();
                break;
            //G:: factor-a ::= not factor
            case Tag.NOT:
                eat(Tag.NOT); factor();
                break;
            //G:: factor-a ::= "-"  factor
            case Tag.MIN:
                eat(Tag.MIN); factor();
                break;
            default:
                error("Error in: factorA");
        }
    }

    private void factor(){
        switch(tag) {
            //G:: factor ::= identifier 
            case Tag.ID:
                vs.checkExprIDType(tok, line); eat(Tag.ID);
                break;
            //G:: factor ::= constant 
            case Tag.NUM:
            case Tag.LIT:
                constant();
                break;
            //G:: factor ::= "(" expression ")"
            case Tag.AP:
                eat(Tag.AP); condition(); eat(Tag.FP);
                break;
            default:
                error("Error in: factor");
        }
    }

    private void relop(){
        switch(tag) {
            //G:: relop ::= "="
            case Tag.EQ:
                eat(Tag.EQ);
                break;
            //G:: relop ::= ">"
            case Tag.GT:
                eat(Tag.GT);
                break;
            //G:: relop ::= "<"
            case Tag.LT:
                eat(Tag.LT);
                break;
            //G:: relop ::= "<>"
            case Tag.NE:
                eat(Tag.NE);
                break;
            //G:: relop ::= ">="
            case Tag.GE:
                eat(Tag.GE);
                break;
            //G:: relop ::= "<="
            case Tag.LE:
                eat(Tag.LE);
                break;
            default:
                error("Error in: relop");
        }
    }

    private void addop(){
        switch(tag) {
            //G:: addop ::= "+"
            case Tag.SUM:
                vs.checkStrOp(tok, line); eat(Tag.SUM);
                break;
            //G:: addop ::= "-"
            case Tag.MIN:
                vs.checkStrOp(tok, line); eat(Tag.MIN);
                break;
            //G:: addop ::= "OR"
            case Tag.OR:
                vs.checkStrOp(tok, line); vs.endStartingCondition(); eat(Tag.OR); vs.setStartingCondition();
                break;
            default:
                error("Error in: addop");
        }
    }

    private void mulop(){
        switch(tag) {
            //G:: mulop ::= "*"
            case Tag.MUL:
                vs.checkStrOp(tok, line); eat(Tag.MUL);
                break;
            //G:: mulop ::= "/"
            case Tag.DIV:
                vs.checkStrOp(tok, line); eat(Tag.DIV);
                break;
            //G:: mulop ::= "AND"
            case Tag.AND:
                vs.checkStrOp(tok, line); vs.endStartingCondition(); eat(Tag.AND); vs.setStartingCondition();
                break;
            default:
                error("Error in: mulop");
        }
    }

    private void constant() {
        switch (tag) {
            //G:: constant ::= integer_const
            case Tag.NUM:
                vs.checkExprNLType(tok, line); eat(Tag.NUM);
                break;
            //G:: constant ::= literal
            case Tag.LIT:
                vs.checkExprNLType(tok, line); eat(Tag.LIT);
                break;
            default:
                error("Error in: constant");
        }
    }
}
