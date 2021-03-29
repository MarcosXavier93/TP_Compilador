package analisador_sintatico;

import analisador_lexico.Tag;
import analisador_lexico.Token;
import java.util.ArrayList;
import java.util.Hashtable;

public class Parser {
    private Token tok;
    private int tag;
    private int i;
    private int line;
    private int curType;
    private int resultExprType;
    private ArrayList<Token> tokens = new ArrayList<Token> ();
    
    public Parser(ArrayList<Token> tokens){
        this.tokens=tokens;
        i=0;
        tok=tokens.get(i);
        tag=tok.tag;
        line=tok.line;
        curType = Tag.VOID;
        resultExprType = Tag.VOID;
    }

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
        System.out.print(""+funcaoDoErro+" Error(" + line + "): Token não esperado:"); //debug
        tok.imprimeToken(tok);
        while(tag!=Tag.EOF)
            advance();
    } 

    /* Seta o tipo basico com que se esta trabalhando em uma expressao de atribuicao */
    public void setCurType(int tipo){
        curType = tipo;
    }

    public void resetResultExprType(){
        resultExprType = Tag.VOID;
    }

    private void eat(int t){
        if(tag==t){
            //Checa se a tag e um tipo basico
            if(tag == Tag.INT || tag == Tag.STR){
                setCurType(tag);
            }
            //Caso ; deve resetar o resultado esperado de uma expressao
            if(tag == Tag.PV){
                resetResultExprType();
            }
            //System.out.print("Token Consumido("+line+"): ");
            //tok.imprimeToken(tok);
            advance();
        }
        else error("eat");
    }

    private void program(){
        switch(tag) {
            // D:: program ::= program decl-list stmt-list end
            // N:: program ::= init [decl-list] begin stmt-list stop
            case Tag.INIT:
                eat(Tag.INIT); declList();  //precisa arrumar aqui [decl-list] para quando tem mais de um decl-list/variaveis declarando
                if (tag == Tag.BEGIN)
                    eat(Tag.BEGIN); stmtList();
                
                if (tag == Tag.EOF)
                    System.out.println("Fim de arquivo inesperado.");
                else
                    eat(Tag.STOP);
                    //vs.imprimirTS();
                break;
                
            default:
                error("program");
        }
    }

    private void declList(){
        // D:: decl-list ::= decl decl-list
        // N:: decl-list ::= decl ";" { decl ";"}
    
        //fix decl ";" { decl ";"}
        decl(); 
        switch(tag) {
            case Tag.PV:
                eat(Tag.PV);
                if(tag == Tag.BEGIN){//fim declarações
                    break;
                }else{
                    declList();
                }
                break; 
            default:
                error("declList");
        }   
    }

    private void decl(){      
        identList();   
        switch(tag) {
            //D:: decl ::= type ident-list ";"
            //N:: decl ::= ident-list is type
            case Tag.IS:
                eat(Tag.IS); type();
                break;
            case Tag.VRG:
                eat(Tag.VRG); decl();
                break;
            default:
                error("decl");
        }
    }

    private void identList(){
        switch(tag) {
            //D:: ident-list ::= identifier ident-list'
            //N:: ident-list ::= identifier {"," identifier}
            case Tag.ID:
                eat(Tag.ID); 
                break;
            case Tag.VRG:
                eat(Tag.VRG); eat(Tag.ID);
                break;    
            default:
                error("identList");
        }
    }

    private void type(){
        switch(tag) {
            //D:: type ::= int
            //N:: type ::= int
            case Tag.INT:
                eat(Tag.INT);
                break;
            //D:: type ::= string
            //N:: type ::= string
            case Tag.STR:
                eat(Tag.STR);
                break;
            //D:: type ::= real 
            //N:: type ::= real
            case Tag.REAL:
                eat(Tag.REAL); 
                break;        
            default:
                error("type");
        }
    }

    private void stmtList(){
        switch(tag) {
            //D:: stmt-list ::= stmt stmt-list'
            //N:: stmt-list ::= stmt ";" { stmt ";" }
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                stmt(); eat(Tag.PV); stmtList();
                break;
            case Tag.WHILE:
                break;
            /*case Tag.PV:
                stmt(); eat(Tag.PV); 
                break; */     
            default:
                error("stmtList");
        }
    }

    private void stmt(){
        switch(tag) {
            //D:: stmt ::= assign-stmt ";"
            //N:: stmt ::= assign-stmt
            case Tag.ID:
                assignStmt();
                break;
            //D:: stmt ::= if-stmt
            //N:: stmt ::= if-stmt
            case Tag.IF:
                ifStmt();
                break;
            //D:: stmt ::= while-stmt
            //N:: stmt ::= while-stmt
            case Tag.DO:
                doStmt();
                break;
            //D:: stmt ::= read-stmt ";"
            //N:: stmt ::= read-stmt 
            case Tag.READ:
                readStmt();
                break;
            //D:: stmt ::= write-stmt ";"
            //N:: stmt ::= write-stmt 
            case Tag.WRITE:
                writeStmt();
                break;
            default:
                error("stmt");
        }
    }

    private void assignStmt(){
        switch(tag) {
            //D:: assign-stmt ::= identifier "=" simple_expr
            //N:: assign-stmt ::= identifier ":=" simple_expr
            case Tag.ID:
                eat(Tag.ID); eat(Tag.PPV); simpleExpr();
                break;
            default:
                error("assignStmt");
        }
    }

    private void ifStmt(){
        switch(tag) {
            //D:: if-stmt ::= if  expression  then  stmt-list  if-stmt' end
            //N:: if-stmt ::= if "(" condition ")" begin stmt-list end else begin stmt-list end
            case Tag.IF:
                eat(Tag.IF); eat(Tag.AP); condition(); eat(Tag.FP); eat(Tag.BEGIN); stmtList();
                break;
            case Tag.ELSE:
                eat(Tag.ELSE); eat(Tag.BEGIN); stmtList();
                break;  
            case Tag.END:
                break;    
            default:
                error("ifStmt");
        }
    }

    private void doStmt(){
        switch(tag) {
			//D:: while-stmt ::= do stmt-list stmt-sufix
            //N:: do-stmt ::= do stmt-list do-suffix.
            case Tag.DO:
                eat(Tag.DO); stmtList(); doSufix();
                break;
            default:
                error("doStmt");
        }
    }

    private void doSufix(){
        switch(tag) {
            //D:: stmt-sufix ::= while expression end
            //N:: stmt-sufix ::= while "(" condition ")"
            case Tag.WHILE:
                eat(Tag.WHILE); eat(Tag.AP); condition(); eat(Tag.FP);
                break;
            default:
                error("doSufix");
        }
    }

    private void readStmt(){
        switch(tag) {
            //D:: read-stmt ::= scan  "("  identifier  ")"
            //N:: read-stmt ::= read "(" identifier ")"
            case Tag.READ:
                eat(Tag.READ); eat(Tag.AP); eat(Tag.ID); eat(Tag.FP);
                break;
            default:
                error("redStmt");
        }
    }

    private void writeStmt(){
        switch(tag) {
            //D:: write-stmt ::= print  "("  simple-expr  ")"
            //N:: write-stmt ::= write "(" writable ")"
            case Tag.WRITE:
                eat(Tag.WRITE); eat(Tag.AP); writable(); eat(Tag.FP);
                break;
            default:
                error("writeStmt");
        }
    }

    private void writable() {
        simpleExpr();
    }
    

    private void condition(){
        switch(tag) {
            //D:: expression ::= simple-expr  expression'
            //N:: expression ::= simple-expr | simple-expr relop simple-expr
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
            case Tag.NOT:
            case Tag.MIN:
                simpleExpr();
                break;
              
            default:
                error("condition");
        }
        
        switch(tag){
            case Tag.GT:
            case Tag.LT:
            case Tag.GE:
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
                relop(); simpleExpr();
                break;  
            default:
                error("conditionB");
        }
    }

    private void simpleExpr(){
        switch(tag) {
            //D:: simple-expr ::= term  simple-expr'
            //N:: simple-expr ::= term | simple-expr addop term
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
            case Tag.NOT:
            case Tag.MIN:
                term(); //simpleExpr();
                break;
            default:
                error("simpleExprA");
        }

        switch(tag) {
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
                error("simpleExprB");
        }
    }

    private void simpleExpr_MIN(){
        switch(tag) {
            //D:: simple-expr ::= term  simple-expr'
            //N:: simple-expr ::= term | simple-expr addop term   
            case Tag.MIN:
            case Tag.SUM:
            case Tag.OR:
                addop(); term();
                break;    
            default:
                error("simpleExor_MIN");
        }
    }

    private void term(){
        switch(tag) {
            //D:: term ::= factor-a  term'
            //N:: term ::= factor-a | term mulop factor-a
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
                term(); mulop(); factorA();
            default:
                error("termA");
        }
        
        /*switch(tag) {
            case Tag.MUL:
            case Tag.DIV:
            case Tag.AND:
                term(); mulop(); factorA();  
            default:
                error("termB");
        }*/
    }


    private void factorA(){
        switch(tag) {
            //D:: factor-a ::= factor
            //N:: factor-a ::= factor 
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
                factor();
                break;
            //D:: factor-a ::= !  factor
            //N:: factor-a ::= not factor
            case Tag.NOT:
                eat(Tag.NOT); factor();
                break;
            //D:: factor-a ::= "-"  factor
            //N:: factor-a ::= "-"  factor
            case Tag.MIN:
                eat(Tag.MIN); factor();
                break;
            default:
                error("factorA");
        }
    }

    private void factor(){
        switch(tag) {
            //D:: factor ::= identifier
            //N:: factor ::= identifier 
            case Tag.ID:
                eat(Tag.ID);
                break;
            //D:: factor ::= constant
            //N:: factor ::= constant 
            case Tag.NUM:
            case Tag.LIT:
                constant();
                break;
            //D:: factor ::= "("  expression  ")"
            //N:: factor ::= "(" expression ")"
            case Tag.AP:
                eat(Tag.AP); condition(); eat(Tag.FP);
                break;
            default:
                error("factor");
        }
    }

    private void relop(){
        switch(tag) {
            //D:: relop ::= "=="
            //N:: relop ::= "="
            case Tag.EQ:
                eat(Tag.EQ);
                break;
            //D:: relop ::= ">"
            //N:: relop ::= ">"
            case Tag.GT:
                eat(Tag.GT);
                break;
            //D:: relop ::= "<"
            //N:: relop ::= "<"
            case Tag.LT:
                eat(Tag.LT);
                break;
            //D:: relop ::= "!="
            //N:: relop ::= "<>"
            case Tag.NE:
                eat(Tag.NE);
                break;
            //D:: relop ::= ">="
            //N:: relop ::= ">="
            case Tag.GE:
                eat(Tag.GE);
                break;
            //D:: relop ::= "<="
            //N:: relop ::= "<="
            case Tag.LE:
                eat(Tag.LE);
                break;
            default:
                error("relop");
        }
    }

    private void addop(){
        switch(tag) {
            //D:: addop ::= "+"
            //N:: addop ::= "+"
            case Tag.SUM:
                eat(Tag.SUM);
                break;
            //D:: addop ::= "-"
            //N:: addop ::= "-"
            case Tag.MIN:
                eat(Tag.MIN);
                break;
            //D:: addop ::= "||"
            //N:: addop ::= "OR"
            case Tag.OR:
                eat(Tag.OR);
                break;
            default:
                error("addop");
        }
    }

    private void mulop(){
        switch(tag) {
            //D:: mulop ::= "*"
            //N:: mulop ::= "*"
            case Tag.MUL:
                eat(Tag.MUL);
                break;
            //D:: mulop ::= "/"
            //N:: mulop ::= "/"
            case Tag.DIV:
                eat(Tag.DIV);
                break;
            //D:: mulop ::= "&&"
            //N:: mulop ::= "AND"
            case Tag.AND:
                eat(Tag.AND);
                break;
            default:
                error("mulop");
        }
    }

    private void constant() {
        switch (tag) {
            //D:: constant ::= integer_const
            //N:: constant ::= integer_const
            case Tag.NUM:
                eat(Tag.NUM);
                break;
            //D:: constant ::= literal
            //N:: constant ::= literal
            case Tag.LIT:
                eat(Tag.LIT);
                break;
            default:
                error("constant");
        }
    }
}
