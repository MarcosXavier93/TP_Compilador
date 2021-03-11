package analisador_sintatico;

import analisador_lexico.Tag;
import analisador_lexico.Token;
import analisador_lexico.Num;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Parser {
    private int curType;
    private int resultExprType;
    private int curConditionType;
    private boolean startingCondition;

    private Token tok;
    private int tag;
    private int i;
    private int line;
    private ArrayList<Token> tokens = new ArrayList<Token> ();
    
    private Hashtable<String, Integer> TS = new Hashtable<>();

    public Parser(ArrayList<Token> tokens){
        this.tokens=tokens;
        i=0;
        tok=tokens.get(i);
        tag=tok.tag;
        line=tok.line;

        curType = Tag.VOID;
        resultExprType = Tag.VOID;
        curConditionType = Tag.VOID;
        startingCondition = false;
    }

    public Hashtable<String, Integer> getTS() {
        return TS;
    }

    public void init(){
        program();
    };

    private String type2String(int type){
        String tipo = "int";
        //Definir o tipo
        if(type == Tag.STR){
            tipo = "string";
        }
        return tipo;
    }
    
    private void wrongExprType(int line, String tipo, String lexeme, String tokTipo, int tipoEsperado){
        System.out.println("Error(" + line + "): Tipo '" + tipo + "' do "+ tokTipo +" '" + lexeme + "' incompatível com a expressão.");
        System.out.println("Esperava '"+type2String(tipoEsperado)+"'");
        System.out.println("Fim de arquivo inesperado.");
        System.exit(0);
    }


    private void advance(){
        i++;
        tok=tokens.get(i);
        tag=tok.tag;
        line=tok.line;
    }

    private void error(){
        if(tag==Tag.EOF) {
            if(line==-4)
                System.out.println("Arquivo de entrada vazio");
            return;
        }
        System.out.print("Error(" + line + "): Token não esperado:");
        tok.imprimeToken(tok);
        while(tag!=Tag.EOF)
            advance();
    }

    private void eat(int t){
        if(tag==t){
            //Checa se a tag e um tipo basico
            if(tag == Tag.INT || tag == Tag.STR){
                curType = t;
            }
            //Caso ; deve resetar o resultado esperado de uma expressao
            if(tag == Tag.PV){
                resultExprType = Tag.VOID;
            }
            //System.out.print("Token Consumido("+line+"): ");
            //tok.imprimeToken(tok);
            advance();
        }
        else error();
    }

    /* Indica o inicio de uma expr de condicao */
    public void setStartingCondition(){
        startingCondition = true;
    }

    /* Indica o fim de uma expr de condicao */
    public void endStartingCondition(){
        startingCondition = false;
        curConditionType = Tag.VOID;
    }
    
      /* Checa se o tipo dos termos (identificador) que estao formando a expressao estao corretos */
    public void checkExprIDType(Token tok, int line){
        // Caso trabalhando com um expr de condicao
        if(startingCondition){
            // Caso nao tenha identificado o tipo do primeiro termo da condicao
            if(curConditionType == Tag.VOID){
                // Checa se o ID existe na TS
                if(!TS.containsKey(tok.getLexeme())){
                    System.out.println("Error(" + line + "): Identificador '"+tok.getLexeme()+"' não declarado");
                    System.out.println("Fim de arquivo inesperado.");
                    System.exit(0);
                }else{
                    // Caso o identificador exista na TS pega o tipo dele
                    curConditionType = TS.get(tok.getLexeme());
                }
            }else{
                // Caso ja tenha identificado o primeiro termo da condicao tem que checar o segundo termo
                // se o segundo termo tem tipo igual ao primeiro
                // Checa se o ID existe na TS
                if(!TS.containsKey(tok.getLexeme())){
                    System.out.println("Error(" + line + "): Identificador '"+tok.getLexeme()+"' não declarado");
                    System.out.println("Fim de arquivo inesperado.");
                    System.exit(0);
                }else{
                    // Caso o identificador exista na TS pega o tipo dele
                    int segundoTermoTipo = TS.get(tok.getLexeme());
                    if(curConditionType != segundoTermoTipo){
                        wrongExprType(line, type2String(segundoTermoTipo), tok.getLexeme(), "identificador", curConditionType);
                    }
                }
            }
        }
        // Se resultExprType tem um valor diferente de VOID quer dizer que se esta trabalhando com uma assign-stmt
        if(resultExprType != Tag.VOID) {
            //Caso id nao tenha sido declarado
            if (!TS.containsKey(tok.getLexeme())) {
                System.out.println("Error(" + line + "): Identificador '" + tok.getLexeme() + "' não declarado");
                System.out.println("Fim de arquivo inesperado.");
                System.exit(0);
            } else {
                //Caso seja um identificador
                int idType = TS.get(tok.getLexeme());
                if (resultExprType != idType) {
                    wrongExprType(line, type2String(idType), tok.getLexeme(), "identificador", resultExprType);
                }
            }
        }
    }

    /* Checa se o tipo dos termos (num ou literal - NL) que estao formando a expressao estao corretos */
    public void checkExprNLType(Token tok, int line){
        // Caso trabalhando com um expr de condicao
        if(startingCondition){
            // Caso nao tenha identificado o tipo do primeiro termo da condicao
            if(curConditionType == Tag.VOID){
                //Caso seja um NUM
                if (tok instanceof Num) {
                    curConditionType = Tag.INT;
                } else {
                    //Caso contrario so pode ser um lit
                    curConditionType = Tag.STR;
                }
            }else{
                // Caso ja tenha identificado o primeiro termo da condicao tem que checar
                // se o segundo termo tem tipo igual ao primeiro
                //Caso seja um NUM
                if (tok instanceof Num) {
                    if(curConditionType != Tag.INT){
                        wrongExprType(line, "int", String.valueOf(((Num) tok).value), "num", curConditionType);
                    }
                } else {
                    if(curConditionType != Tag.STR){
                        wrongExprType(line, "string", tok.getLexeme(), "literal", curConditionType);
                    }
                }
            }
        }

        // Se resultExprType tem um valor diferente de VOID quer dizer que se esta trabalhando com uma assign-stmt
        if(resultExprType != Tag.VOID) {
            //Caso seja um  NUM
            if (tok instanceof Num) {
                if (resultExprType != Tag.INT) {
                    wrongExprType(line, "int", String.valueOf(((Num) tok).value), "num", resultExprType);
                }
            } else {
                //Caso contrario so pode ser um lit
                if (resultExprType != Tag.STR) {
                    wrongExprType(line, "string", tok.getLexeme(),  "literal", resultExprType);
                }
            }
        }
    }

    /* Determina qual é o tipo do identificador que recebera o valor da expressao */
    public void setCurAssignStmtType(Token tok, int line){
        if(resultExprType == Tag.VOID){
            //Caso id nao tenha sido declarado
            if(!TS.containsKey(tok.getLexeme())){
                System.out.println("Error(" + line + "): Identificador '"+tok.getLexeme()+"' não declarado");
                System.out.println("Fim de arquivo inesperado.");
                System.exit(0);
            }else{
                //Uma nova expressao de igualdade sera criada entao muda o tipo esperado da var que recebe
                //o resultado final
                resultExprType = TS.get(tok.getLexeme());
            }
        }
    }

    private void wrogStrOp(int line, String op){
        System.out.println("Error(" + line + "): Operação '"+ op +"' inválida com 'string'");
        System.exit(0);
    }

    public void putTS(Token tok, int line){
        //Veriifica se tok ja foi declarado na TS
        if(TS.containsKey(tok.getLexeme())){
            System.out.println("Error(" + line + "): Redefinição de '"+tok.getLexeme()+"'");
            System.out.println("Fim de arquivo inesperado.");
            System.exit(0);
        }else{
            TS.put(tok.getLexeme(), curType);
        }
    }

    public void imprimirTS(){
        System.out.println("\n\n\n**** Identificadores na Tabela de símbolos ****\nEntrada - Tipo");
        for (Map.Entry<String, Integer> entrada: TS.entrySet()) {
            System.out.println(entrada.getKey()+" - "+type2String(entrada.getValue()));
        }
    }
    
     /* Checa se a operacao realizada na string esta correta ('+' é a unica operação aceita) */
     public void checkStrOp(Token tok, int line){
        if(resultExprType == Tag.STR){
            switch (tok.tag){
                case Tag.MIN:
                    wrogStrOp(line, "-");
                    break;
                case Tag.OR:
                    wrogStrOp(line, "||");
                    break;
                case Tag.MUL:
                    wrogStrOp(line, "*");
                    break;
                case Tag.DIV:
                    wrogStrOp(line, "/");
                    break;
                case Tag.AND:
                    wrogStrOp(line, "&&");
                    break;
            }
        }
    }
    
    private void program(){
        switch(tag) {
            // program ::= program decl-list stmt-list end
            case Tag.INIT:
                eat(Tag.INIT); declList(); stmtList();
                if (tag == Tag.EOF)
                    System.out.println("Fim de arquivo inesperado.");
                else
                    eat(Tag.END);
                    imprimirTS();
                break;
            default:
                error();
        }
    }

    private void declList(){
        switch(tag) {
            // decl-list ::= decl decl-list
            case Tag.INT:
            case Tag.STR:
                decl(); declList();
                break;
            //decl-list ::= λ
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                break;
            default:
                error();
        }
    }

    private void decl(){
        switch(tag) {
            //decl ::= type ident-list ";"
            case Tag.INT:
            case Tag.STR:
                type(); identList(); eat(Tag.PV);
                break;
            default:
                error();
        }
    }

    private void identList(){
        switch(tag) {
            //ident-list ::= identifier ident-list'
            case Tag.ID:
                putTS(tok, line); eat(Tag.ID); identListPrime();
                break;
            default:
                error();
        }
    }

    private void identListPrime(){
        switch(tag) {
            //ident-list' ::= "," identifier ident-list'
            case Tag.VRG:
                eat(Tag.VRG); putTS(tok, line); eat(Tag.ID); identListPrime();
                break;
            //ident-list' λ
            case Tag.PV:
                break;
            default:
                error();
        }
    }

    private void type(){
        switch(tag) {
            //type ::= int
            case Tag.INT:
                eat(Tag.INT);
                break;
            //type ::= string
            case Tag.STR:
                eat(Tag.STR);
                break;
            default:
                error();
        }
    }

    private void stmtList(){
        switch(tag) {
            //stmt-list ::= stmt stmt-list'
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                stmt(); stmtListPrime();
                break;
            default:
                error();
        }
    }

    private void stmtListPrime(){
        switch(tag) {
            //stmt-list' ::= stmt stmt-list'
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                stmt(); stmtListPrime();
                break;
            //stmt-list' ::= λ
            case Tag.ELSE:
            case Tag.WHILE:
            case Tag.END:
                break;
            default:
                error();
        }
    }

    private void stmt(){
        switch(tag) {
            //stmt ::= assign-stmt ";"
            case Tag.ID:
                assignStmt(); eat(Tag.PV);
                break;
            //stmt ::= if-stmt
            case Tag.IF:
                ifStmt();
                break;
            //stmt ::= while-stmt
            case Tag.DO:
                whileStmt();
                break;
            //stmt ::= read-stmt ";"
            case Tag.READ:
                readStmt(); eat(Tag.PV);
                break;
            //stmt ::= write-stmt ";"
            case Tag.WRITE:
                writeStmt(); eat(Tag.PV);
                break;
            default:
                error();
        }
    }

    private void assignStmt(){
        switch(tag) {
            //assign-stmt ::= identifier "=" simple_expr
            case Tag.ID:
                setCurAssignStmtType(tok, line); eat(Tag.ID); eat(Tag.EQ); simpleExpr();
                break;
            default:
                error();
        }
    }

    private void ifStmt(){
        switch(tag) {
            //if-stmt ::= if  expression  then  stmt-list  if-stmt' end
            case Tag.IF:
                eat(Tag.IF); setStartingCondition(); expression(); endStartingCondition(); stmtList(); ifStmtPrime(); eat(Tag.END);
                break;
            default:
                error();
        }
    }

    private void ifStmtPrime(){
        switch(tag) {
            //if-stmt' ::= else stmt-list
            case Tag.ELSE:
                eat(Tag.ELSE); stmtList();
                break;
            //if-stmt' ::= λ
            case Tag.END:
                break;
            default:
                error();
        }
    }

    private void whileStmt(){
        switch(tag) {
			//while-stmt ::= do stmt-list stmt-sufix
            case Tag.DO:
                eat(Tag.DO); stmtList(); stmtSufix();
                break;
            default:
                error();
        }
    }

    private void stmtSufix(){
        switch(tag) {
            //stmt-sufix ::= while expression end
            case Tag.WHILE:
                eat(Tag.WHILE); setStartingCondition(); expression(); endStartingCondition(); eat(Tag.END);
                break;
            default:
                error();
        }
    }

    private void readStmt(){
        switch(tag) {
            //read-stmt ::= scan  "("  identifier  ")"
            case Tag.READ:
                eat(Tag.READ); eat(Tag.AP); eat(Tag.ID); eat(Tag.FP);
                break;
            default:
                error();
        }
    }

    private void writeStmt(){
        switch(tag) {
            //write-stmt ::= print  "("  simple-expr  ")"
            case Tag.WRITE:
                eat(Tag.WRITE); eat(Tag.AP); simpleExpr(); eat(Tag.FP);
                break;
            default:
                error();
        }
    }

    private void expression(){
        switch(tag) {
            //expression ::= simple-expr  expression'
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.MIN:
            case Tag.NOT:
            case Tag.AP:
                simpleExpr(); expressionPrime();
                break;
            default:
                error();
        }
    }

    private void expressionPrime(){
        switch(tag) {
            //expression' ::= relop  simple-expr
            case Tag.GT:
            case Tag.LT:
            case Tag.LIT:
            case Tag.GE:
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
                relop(); simpleExpr();
                break;
            //expression' ::= λ
            case Tag.END:
            case Tag.FP:
                break;
            default:
                error();
        }
    }

    private void simpleExpr(){
        switch(tag) {
            //simple-expr ::= term  simple-expr'
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.MIN:
            case Tag.NOT:
            case Tag.AP:
                term();  simpleExprPrime();
                break;
            default:
                error();
        }
    }

    private void simpleExprPrime(){
        switch(tag) {
            //simple-expr' ::= addop term simple-expr'
            case Tag.MIN:
            case Tag.SUM:
            case Tag.OR:
                addop(); term(); simpleExprPrime();
                break;
            //simple-expr' ::= λ
            case Tag.END:
            case Tag.GT:
            case Tag.LT:
            case Tag.FP:
            case Tag.GE:
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
            case Tag.PV:
                break;
            default:
                error();
        }
    }

    private void term(){
        switch(tag) {
            //term ::= factor-a  term'
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.MIN:
            case Tag.NOT:
            case Tag.AP:
                factorA(); termPrime();
                break;
            default:
                error();
        }
    }

    private void termPrime(){
        switch(tag) {
            //term' ::= mulop factor-a term'
            case Tag.MUL:
            case Tag.DIV:
            case Tag.AND:
                mulop(); factorA(); termPrime();
                break;
            //term' ::= λ
            case Tag.END:
            case Tag.MIN:
            case Tag.GT:
            case Tag.LT:
            case Tag.SUM:
            case Tag.OR:
            case Tag.FP:
            case Tag.GE:
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
            case Tag.PV:
                break;
            default:
                error();
        }
    }

    private void factorA(){
        switch(tag) {
            //factor-a ::= factor
            case Tag.ID:
            case Tag.NUM:
            case Tag.LIT:
            case Tag.AP:
                factor();
                break;
            //factor-a ::= !  factor
            case Tag.NOT:
                eat(Tag.NOT); factor();
                break;
            //factor-a ::= "-"  factor
            case Tag.MIN:
                eat(Tag.MIN); factor();
                break;
            default:
                error();
        }
    }

    private void factor(){
        switch(tag) {
            //factor ::= identifier
            case Tag.ID:
                checkExprIDType(tok, line); eat(Tag.ID);
                break;
            //factor ::= constant
            case Tag.NUM:
            case Tag.LIT:
                constant();
                break;
            //factor ::= "("  expression  ")"
            case Tag.AP:
                eat(Tag.AP); expression(); eat(Tag.FP);
                break;
            default:
                error();
        }
    }

    private void relop(){
        switch(tag) {
            //relop ::= "=="
            case Tag.EQ:
                eat(Tag.EQ);
                break;
            //relop ::= ">"
            case Tag.GT:
                eat(Tag.GT);
                break;
            //relop ::= "<"
            case Tag.LT:
                eat(Tag.LT);
                break;
            //relop ::= "!="
            case Tag.NE:
                eat(Tag.NE);
                break;
            //relop ::= ">="
            case Tag.GE:
                eat(Tag.GE);
                break;
            //relop ::= "<="
            case Tag.LE:
                eat(Tag.LE);
                break;
            default:
                error();
        }
    }

    private void addop(){
        switch(tag) {
            //addop ::= "+"
            case Tag.SUM:
                eat(Tag.SUM);
                break;
            //addop ::= "-"
            case Tag.MIN:
                checkStrOp(tok, line); eat(Tag.MIN);
                break;
            //addop ::= "||"
            case Tag.OR:
                checkStrOp(tok, line); endStartingCondition(); eat(Tag.OR); setStartingCondition();
                break;
            default:
                error();
        }
    }

    private void mulop(){
        switch(tag) {
            //mulop ::= "*"
            case Tag.MUL:
                checkStrOp(tok, line); eat(Tag.MUL);
                break;
            //mulop ::= "/"
            case Tag.DIV:
                checkStrOp(tok, line); eat(Tag.DIV);
                break;
            //mulop ::= "&&"
            case Tag.AND:
                checkStrOp(tok, line); endStartingCondition(); eat(Tag.AND); setStartingCondition();
                break;
            default:
                error();
        }
    }

    private void constant() {
        switch (tag) {
            //constant ::= integer_const
            case Tag.NUM:
                checkExprNLType(tok, line); eat(Tag.NUM);
                break;
            //constant ::= literal
            case Tag.LIT:
                checkExprNLType(tok, line); eat(Tag.LIT);
                break;
            default:
                error();
        }
    }

}
