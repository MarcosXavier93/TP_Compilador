package gerador_codigo;

import analisador_lexico.Tag;
import analisador_lexico.Token;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class GeradorCodigo {
    private String nome_arquivo;
    private ArrayList<Token> tokens;
    private String codigo;
    private HashMap<String, Integer> variaveis;
    private Iterator<Token> it;
    private Token token;
    private int offset;
    private int cont_label = 'A';
    private int label_atual = 0;
    private Hashtable<String, Integer> TS;


    HashMap<Integer, Integer> labels = new HashMap<>();  // guarda labelDestino -> labelVolta

    public GeradorCodigo(String nome_arquivo, ArrayList<Token> tokens, Hashtable<String, Integer> TS) {
        this.nome_arquivo = nome_arquivo;
        this.tokens = tokens;
        this.TS = TS;
        variaveis = new HashMap<>();
        it = tokens.iterator();
        offset=0;
        codigo = "";
    }

    public void tratarDeclList() {
        int qtd = 0;
        while(token.tag == Tag.INT || token.tag == Tag.STR || token.tag == Tag.REAL) {
            do {
                token = it.next();
                variaveis.put(token.getLexeme(), offset);
                offset++;
                token = it.next();
                qtd++;
            } while (it.hasNext() && token.tag != Tag.PV);
         
            token = it.next(); // Consome PV
        }
        // Empilha o valor inteiro 0
        codigo += "PUSHN " + qtd + '\n';
        //token = it.next(); // Consome PV
    }

    public boolean tratarSimpleExpression() {
        //D: simple-expr			::= term  simple-expr'
        //N: simple-expr            ::= term | simple-expr addop term
        boolean str = tratarTerm();

        int tag = token.getTag();
        if(tag == Tag.SUM || tag == Tag.MIN || tag == Tag.OR){
            token = it.next(); // Consome + ou - ou ||

            // Operacao
            if(tag == Tag.SUM || tag == Tag.OR) {
              
                if(str) {
                    codigo += "SWAP" + '\n';
                    codigo += "CONCAT" + '\n';
                }
                else
                    codigo += "ADD" + '\n';
            }else if (tag == Tag.MIN){
                codigo += "SUB" + '\n';
                //else Tem que ver o operando para &&
            }
            tratarTerm();
        }
        return str;
    }

    public boolean tratarTerm() {
        //D: term                ::= factor-a  term'
        //N: term                ::= factor-a | term mulop factor-a 
        boolean str = tratarFactorA();

        int tag = token.getTag();
        if(tag == Tag.MUL || tag == Tag.DIV || tag == Tag.AND){
            token = it.next(); // Consome * ou / ou &&
        
            // Operacao
            if(tag == Tag.MUL || tag == Tag.AND) {
                codigo += "MUL" + '\n';
            } else if (tag == Tag.DIV) {
                codigo += "DIV" + '\n';
            }
            tratarFactorA();
        }
        return str;
    }

    public boolean tratarFactorA() {
        //D: factor-a			::= factor  |  !  factor  |  "-"  factor
        //N: factor-a           ::= factor | not factor | "-" factor
        boolean menos=false, negativo=false;

        if(token.tag == Tag.MIN) {
            menos = true;
            token = it.next(); // Consome -
        }
        else if(token.tag == Tag.NOT) {
            negativo = true;
            token = it.next(); // Consome !
        }

        boolean str = tratarFactor();
        if(menos) {
            codigo += "PUSHI -1" + '\n';
            codigo += "MUL" + '\n';

        } else if(negativo){
            codigo += "NOT\n";
        }
        return str;
    }

    public boolean tratarFactor() {
        //D: factor          	::= identifier  |  constant  |  "("  expression  ")"
        //N: factor          	::= identifier  |  constant  |  "("  expression  ")"
        boolean str = false;
        switch (token.getTag()) {
            case Tag.ID:
                if(TS.get(token.getLexeme())==Tag.STR)
                    str=true;
                int pos = variaveis.get(token.getLexeme());
                // pega o valor da variavel
                codigo += "PUSHG " + pos + '\n';
                token = it.next(); // Consome PV
                break;
            case Tag.NUM:
                codigo += "PUSHI " + token.toString() + "\n";
                token = it.next(); // Consome PV
                break;
            case Tag.LIT:
                str=true;
                codigo += "PUSHS " + token.getLexeme() + '\n';
                token = it.next(); // Consome PV
                break;
            case Tag.AP:
                token = it.next(); // Consome AP
                tratarExpression();
                token = it.next(); // Consome FP
                break;
        }
        return str;
    }

    public void tratarExpression() {
        //D: expression			::= simple-expr  expression'
        //N: expression			::= simple-expr | simple-expr relop simple-expr
        tratarSimpleExpression();

        int tag = token.getTag();
        if(tag == Tag.EQ || tag == (int)'>' || tag == (int)'<' || tag == Tag.NE || tag == Tag.GE || tag == Tag.LE  ){
            int tagRelop = tratarRelop();

            tratarSimpleExpression();
            switch (tagRelop) {
                case Tag.EQ:    // ==
                    codigo += "EQUAL\n";
                    break;
                case (int) '>':
                    codigo += "SUP\n";
                    break;
                case (int) '<':
                    codigo += "INF\n";
                    break;
                case Tag.NE:    // !=
                    codigo += "EQUAL\n";
                    codigo += "NOT\n";
                    break;
                case Tag.GE:    // >=
                    codigo += "SUPEQ\n";
                    break;
                case Tag.LE:    // <=
                    codigo += "INFEQ\n";
                    break;
            }
        }
    }

    public int tratarRelop(){
        //relop				::= "==" |  ">" | "<" | "<>" | ">=" | "<="
        int tag = token.getTag();
        switch (tag) {
            case Tag.EQ:    // ==
                break;
            case (int)'>':
                break;
            case (int)'<':
                break;
            case Tag.NE:    // <>
                break;
            case Tag.GE:    // >=
                break;
            case Tag.LE:    // <=
                break;
            default:        
                break;
        }
        token = it.next(); // Consome o operador
        return tag;
    }

    public void tratarStmtList() {
        //N: stmt-list ::= stmt ";" { stmt ";" }

        tratarStmt();
        switch (token.tag){
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                tratarStmtList();
                break;
        }
        token = it.next(); // Consome PV
    }

    public void tratarStmt() {
        //N: stmt ::= assign-stmt | if-stmt | do-stmt | read-stmt | write-stmt

        switch (token.getTag()) {
            case Tag.ID:
                tratarAssign();
                break;
            case Tag.IF:
                tratarIf();
                break;
            case Tag.DO:
                tratarWhile();
                break;
            case Tag.READ:
                tratarRead();
                break;
            case Tag.WRITE:
                tratarWrite();
                break;
        }
    }

    public void tratarCondition() {
        tratarExpression();
    }

    public void tratarIf() {
        // N: if-stmt ::= if "(" condition ")" begin stmt-list end | if "(" condition ")" begin stmt-list end else begin stmt-list end
        
        token = it.next();  // Consome if
        token = it.next(); // Consome Abre Parênteses.
        int destino = cont_label++;
        int fim = cont_label++;
        tratarCondition();
        token = it.next();  // Consome Fecha Parênteses.
        codigo += "JZ " + (char)destino + '\n';
        token = it.next();  // Consome begin
        tratarStmtList();
        token = it.next();  // Consome end  

        if(token.tag == Tag.ELSE) {
            token = it.next();  // Consome ELSE
            token = it.next();  // Consome begin
            codigo += "JUMP " + (char)fim + '\n';
            codigo += (char)destino + ":\n";
            tratarStmtList();
            codigo += (char)fim + ":\n";
            token = it.next();  // Consome end
        
        }else{
            codigo += (char)destino + ":\n";
        }
    }

    public void tratarWhile() {
        //D: while-stmt			::= do stmt-list stmt-sufix
        //N: while-stmt         ::= do stmt-list do-suffix
        token = it.next(); //Consome DO
        int destino = cont_label++;
        label_atual = destino;
        codigo += (char)destino + ":\n";
        tratarStmtList();
        tratarStmtDo();
        codigo += "NOT\n";  // Nega o que está no topo da pilha pois o jz verifica se é 0 para saltar
        codigo += "JZ " + (char)destino + '\n';
    }

    public void tratarStmtDo() {
        //N: do-sufix			::= while "(" condition ")" end
        token = it.next(); //Consome WHILE
        token = it.next(); //Consome AP
        tratarCondition();
        token = it.next(); //Consome FP
        token = it.next(); //Consome END
    }

    public void tratarRead() {
        //N: read-stmt          ::= read "(" identifier ")" 

        token = it.next(); //Consome Scan
        token = it.next(); //Consome AP
        int pos = variaveis.get(token.getLexeme());
        boolean str=false;
        if(TS.get(token.getLexeme())==Tag.STR)
            str=true;
        token = it.next(); //Consome ID
        token = it.next(); //Consome FP
        codigo += "READ\n";
        if(!str)
            codigo += "ATOI" + '\n';
        codigo += "STOREL " + pos + '\n';
    }

    public boolean writable() {
        boolean str = tratarSimpleExpression();
        return str;
    }

    public void tratarWrite() {
        //N: write-stmt          ::= write "(" writable ")" 

        token = it.next(); //Consome PRINT
        token = it.next(); //Consome AP
        boolean str = writable();
        token = it.next(); //Consome FP
        if(str)
            codigo += "WRITES\n";
        else
            codigo += "WRITEI\n";
    }

    public void tratarAssign() {
        //D: assign-stmt			::= identifier "=" simple_expr
        //N: assign-stmt			::= identifier ":=" simple_expr
        int pos = variaveis.get(token.getLexeme());
        // Pega proximo token
        token = it.next(); // Consome :=
        tratarSimpleExpression();
        codigo += "STOREL " + pos + '\n';
    }

    // Varre os tokens e gera o código referente a eles
    public void gerar() {
        token = it.next();
        codigo += "INIT\n";
        tratarDeclList();
        tratarStmtList();
        token = it.next();
        codigo += "STOP\n";
        gravarCodigo();
    }


    public void gravarCodigo() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(nome_arquivo, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.write(codigo);
        writer.close();
    }

    
}
