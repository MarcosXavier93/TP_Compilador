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

public class ErrorMessages {
    
    public static String CARACTERE_INVALIDO = "Caractere invalido";
    public static String LEXEMA_NAO_IDENTIFICADO = "Lexema nao identificado";
    public static String FIM_ARQUIVO_NAO_ESPERADO = "Fim do arquivo nao esperado";
    public static String TOKEN_NAO_ESPERADO = "Token nao esperado";
    public static String IDENTIFICADOR_NAO_DECLARADO = "Identificador nao declarado";
    public static String IDENTIFICADOR_JA_DECLARADO = "Identificador ja declarado";
    public static String CLASSE_IDENTIFICADOR_INCOMPATIVEL = "Classe de identificador incompativel";
    public static String TIPOS_INCOMPATIVEIS = "Tipos incompativeis";
    public static String TAMANHO_VETOR_MAXIMO = "Tamanho do vetor excede o maximo permitido";

    public static void Printf(String message, long line, String lexeme){
        
        String menssageLexeme = lexeme == null ? "" : " [" + lexeme + "]";
        System.out.println(line + ":" + message + menssageLexeme);
        System.exit(0);
    }
}