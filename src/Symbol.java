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

public class Symbol {
    
        private String lexeme = ""; // Lexema
        private byte token; // Numero do Token
        private byte type; // Tipo da variavel
        private byte category; // Classe
        private byte size;
        private int address; // Endereco na tabela
    
        public static final byte NO_CATEGORY = 0;
        public static final byte CATEGORY_VAR = 1;
        public static final byte CATEGORY_CONST = 2;
    
        public static final byte NO_TYPE = 0;
        public static final byte TYPE_INTEGER = 1;
        public static final byte TYPE_CARACTER = 2;
        public static final byte TYPE_STRING = 3;
        public static final byte TYPE_ARRAY = 4;
        public static final byte TYPE_LOGICAL = 5;

        public Symbol(){
    
        }
    
        public Symbol(byte token, String lexeme) {
            this.token = token;
            this.lexeme = lexeme; 
        }
    
        public Symbol(byte token, String lexeme, byte type) {
            this.token = token;
            this.lexeme = lexeme; 
            this.type = type;
            this.category = NO_CATEGORY;
            this.size = -1;
        }
    
        public String getLexeme() {
            return lexeme;
        }
    
        public void setLexeme(String lexeme) {
            this.lexeme = lexeme;
        }
    
        public byte getToken() {
            return token;
        }
    
        public void setToken(byte token) {
            this.token = token;
        }
    
        public int getSize() {
            return size;
        }
    
        public void setSize(byte size){
            this.size = size;
        }
    
        public byte getCategory(){
            return category;
        }
    
        public void setCategory(byte category){
            this.category = category;
        }
    
        public byte getType(){
            return type;
        }
    
        public void setType(byte type){
            this.type = type;
        }

        public int getAddress(){
            return address;
        }
    
        public void setAddress(int address){
            this.address = type;
        }
    }
    