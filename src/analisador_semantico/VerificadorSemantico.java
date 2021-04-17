package analisador_semantico;
  
import java.util.Hashtable;
import analisador_lexico.*;
import java.util.ArrayList;
import java.util.Map; 

public class VerificadorSemantico {
    private int curType;
    private int resultExprType;
    private int curConditionType;
    private boolean startingCondition;
    private Hashtable<String, Integer> TS = new Hashtable<>();
    private ArrayList<Token> tokens = new ArrayList<Token> ();

    public VerificadorSemantico(){
        curType = Tag.VOID;
        resultExprType = Tag.VOID;
        curConditionType = Tag.VOID;
        startingCondition = false;
    }

    public void putTS(Token tok, int line){
        //Veriifica caso o token ja esta inserido na Tabela de Simbolos
        if(TS.containsKey(tok.getLexeme())){
            System.out.println("Error(" + line + "): Reuso de variável '"+tok.getLexeme()+"'");
            System.out.println("Fim de arquivo inesperado.");
            System.exit(0);
        }else{
            tokens.add(tok);
            TS.put(tok.getLexeme(), curType);
        }
    }

    public Hashtable<String, Integer> getTS() {
        return TS;
    }

    private String type2String(int type){
        String tipo = "integer";
        //Definir o tipo
        if(type == Tag.STR){
            tipo = "string";
        } else if ( type == Tag.REAL) 
        {
            tipo = "real";
        }
        return tipo;
    }

    public void imprimirTS(){
        System.out.println("\n\n\n**** Identificadores na Tabela de símbolos ****\nEntrada - Tipo");
        for (Map.Entry<String, Integer> entrada: TS.entrySet()) {
            System.out.println(entrada.getKey()+" - "+type2String(entrada.getValue()));
        }
    }


    private void wrongExprType(int line, String tipo, String lexeme, String tokTipo, int tipoEsperado){
        System.out.println("Error(" + line + "): Tipo '" + tipo + "' do "+ tokTipo +" '" + lexeme + "' incompatível com a expressão.");
        System.out.println("Esperava '"+type2String(tipoEsperado)+"'");
        System.out.println("Fim de arquivo inesperado.");
        System.exit(0);
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


    private void wrogStrOp(int line, String op){
        System.out.println("Error(" + line + "): Operação '"+ op +"' inválida com 'string'");
        System.exit(0);
    }

    /* Checa se a operacao é realizada com string, se for da error */
    public void checkStrOp(Token tok, int line){
        if(resultExprType == Tag.STR){
            switch (tok.tag){
                case Tag.SUM:
                    wrogStrOp(line, "+");
                    break;
                case Tag.MIN:
                    wrogStrOp(line, "-");
                    break;
                case Tag.OR:
                    wrogStrOp(line, "or");
                    break;
                case Tag.MUL:
                    wrogStrOp(line, "*");
                    break;
                case Tag.DIV:
                    wrogStrOp(line, "/");
                    break;
                case Tag.AND:
                    wrogStrOp(line, "and");
                    break;
            }
        }
    }

    public void resetResultExprType(){
        resultExprType = Tag.VOID;
    }

    /* Indica o inicio de uma expr de condicao */
    public void setStartingCondition(){
        startingCondition = true;
        //curConditionType = Tag.INT;
    }

    /* Indica o fim de uma expr de condicao */
    public void endStartingCondition(){
        startingCondition = false;
        curConditionType = Tag.VOID;
    }

    /* Seta o tipo basico com que se esta trabalhando em uma condicao de if ou while */
    public void setCurConditionType(int tipo){             ///NAAAAAAAAAAO USADO
        curConditionType = tipo;
    }

    /* Seta o tipo de cada variável declarada */
    public void setCurType(int tipo){
        curType = tipo;
        for(int i=0;i<tokens.size();i++){
            TS.replace(tokens.get(i).toString(), curType);
        }
        tokens.clear();
    }
    
    /* Determina qual é o tipo do identificador que recebera o valor da expressao */
    public void setCurAssignStmtType(Token tok, int line) {
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
}
