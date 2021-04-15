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

import java.util.HashMap;

public class SymbolsTable {

    public final static byte ID = 0;
    public final static byte CONST = 1;
    public final static byte VAR = 2;
    public final static byte INTEGER = 3;
    public final static byte CHAR = 4;
    public final static byte FOR = 5;
    public final static byte IF = 6;
    public final static byte ELSE = 7;
    public final static byte AND = 8;
    public final static byte OR = 9;
    public final static byte NOT = 10;
    public final static byte EQUAL = 11;
    public final static byte TO = 12;
    public final static byte OPEN_PARENT = 13;
    public final static byte CLOSE_PARENT = 14;
    public final static byte LESS_THAN = 15;
    public final static byte MORE_THAN = 16;
    public final static byte DIFFERENT = 17;
    public final static byte MORE_EQUAL = 18;
    public final static byte LESS_EQUAL = 19;
    public final static byte COMMA = 20;
    public final static byte PLUS = 21;
    public final static byte MINUS = 22;
    public final static byte MULTIPLY = 23;
    public final static byte DIVIDE = 24;
    public final static byte DOT_COMMA = 25;
    public final static byte OPEN_KEYS = 26;
    public final static byte CLOSE_KEYS = 27;
    public final static byte THEN = 28;
    public final static byte READ_LN = 29;
    public final static byte STEP = 30;
    public final static byte WRITE = 31;
    public final static byte WRITE_LN = 32;
    public final static byte PERCENT = 33;
    public final static byte OPEN_COLCH = 34;
    public final static byte CLOSE_COLCH = 35;
    public final static byte DO = 36;  
    public final static byte VALUE = 37;
    
    //public int address = 38; // Primeiro registro livre

    public HashMap<String, Symbol> table = new HashMap<>();

    public SymbolsTable() {
        table.put("id", new Symbol(ID, "id"));
        table.put("const", new Symbol(CONST, "const"));
        table.put("var", new Symbol(VAR, "var"));
        table.put("integer", new Symbol(INTEGER, "integer"));
        table.put("char", new Symbol(CHAR, "char"));
        table.put("for", new Symbol(FOR, "for"));
        table.put("if", new Symbol(IF, "if"));
        table.put("else", new Symbol(ELSE, "else"));
        table.put("and", new Symbol(AND, "and"));
        table.put("or", new Symbol(OR, "or"));
        table.put("not", new Symbol(NOT, "not"));
        table.put("=", new Symbol(EQUAL, "="));
        table.put("to", new Symbol(TO, "to"));
        table.put("(", new Symbol(OPEN_PARENT, "("));
        table.put(")", new Symbol(CLOSE_PARENT, ")"));
        table.put("<", new Symbol(LESS_THAN, "<"));
        table.put(">", new Symbol(MORE_THAN, ">"));
        table.put("<>", new Symbol(DIFFERENT, "<>"));
        table.put(">=", new Symbol(MORE_EQUAL, ">="));
        table.put(">=", new Symbol(MORE_EQUAL, ">="));
        table.put("<=", new Symbol(LESS_EQUAL, "<="));
        table.put(",", new Symbol(COMMA, ","));
        table.put("+", new Symbol(PLUS, "+"));
        table.put("-", new Symbol(MINUS, "-"));
        table.put("*", new Symbol(MULTIPLY, "*"));
        table.put("/", new Symbol(DIVIDE, "/"));
        table.put(";", new Symbol(DOT_COMMA, ";"));
        table.put("{", new Symbol(OPEN_KEYS, "{"));
        table.put("}", new Symbol(CLOSE_KEYS, "}"));
        table.put("then", new Symbol(THEN, "then"));
        table.put("readln", new Symbol(READ_LN, "readln"));
        table.put("step", new Symbol(STEP, "step"));
        table.put("write", new Symbol(WRITE, "write"));
        table.put("writeln", new Symbol(WRITE_LN, "writeln"));
        table.put("%", new Symbol(PERCENT, "%"));
        table.put("[", new Symbol(OPEN_COLCH, "["));
        table.put("]", new Symbol(CLOSE_COLCH, "]"));
        table.put("do", new Symbol(DO, "do"));

    }

    /**
     * Scan the table looking for
     * the lexeme, and if it exists
     * return the memory address.
     * If not, return NULL;
     */
    public Symbol searchLexeme(String lexeme) {
        return table.get(lexeme.toLowerCase());
    }


    /**
     * Insert lexeme
     * dynamically on the table
     * and return the memory address.
     */
    public Symbol insertID(String lexeme, byte type) {
        Symbol symbol = new Symbol(ID, lexeme, type);
        table.put(lexeme, symbol);
        return symbol;
    }

    public Symbol insertConst(String lexeme, byte type) {
        Symbol symbol = new Symbol(VALUE, lexeme, type);
        table.put(lexeme, symbol);
        return symbol;
    }

    /**
     * It lists all items on the
     * symbols table.
     * Has to be disabled before delivering.
     */
    public void showSymbolsTable() {
        for (String key : table.keySet()) {
            System.out.println(table.get(key).getToken() + " " + table.get(key).getLexeme());
        }
    }

}