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

public class Letter {

    public static char OPEN_COLCH            = '[';
    public static char CLOSE_COLCH           = ']';
    public static char OPEN_KEYS             = '{';
    public static char CLOSE_KEYS            = '}';
    public static char CLOSE_PARENT          = '(';
    public static char OPEN_PARENT           = ')';
    public static char LESS_THAN             = '<';
    public static char MORE_THAN             = '>';
    public static char EQUAL                 = '=';
    public static char DIVIDE                = '/';
    public static char MULTIPLY              = '*';
    public static char PLUS                  = '+';
    public static char MINUS                 = '-';
    public static char PERCENT               = '%';
    public static char DOT_COMMA             = ';';
    public static char COLON                 = ':';
    public static char COMMA                 = ',';
    public static char DOT                   = '.';
    public static char UNDERSCORE            = '_';
    public static char AT_SIGN               = '@';
    public static char AMPERSAND             = '&';
    public static char CIRCUMFLEX_ACCENT     = '^';
    public static char EXCLAMATION_MARK      = '!';
    public static char QUESTION_MARK         = '?';
    public static char QUOTATION_MARK        = '"';
    public static char APOSTROPHE            = '\'';
    public static char BACK_SPACE            = 8;
    public static char HORIZONTAL_TABULATION = 9;
    public static char LINE_FEED             = 10;
    public static char VERTICAL_TABULATION   = 11;
    public static char CARRIAGE_RETURN       = 13;
    public static char SPACE                 = 32;
    public static char EOF                   = 65535;

    public static boolean IsSpecialCharacter(char symbol){
        return symbol == OPEN_COLCH       || symbol == CLOSE_COLCH      || symbol == OPEN_KEYS        ||
               symbol == CLOSE_KEYS       || symbol == CLOSE_PARENT     || symbol == OPEN_PARENT      ||
               symbol == LESS_THAN        || symbol == MORE_THAN        || symbol == DIVIDE           ||
               symbol == MULTIPLY         || symbol == PLUS             || symbol == MINUS            ||
               symbol == PERCENT          || symbol == DOT_COMMA        || symbol == COLON            ||
               symbol == COMMA            || symbol == AT_SIGN          || symbol == AMPERSAND        ||
               symbol == CIRCUMFLEX_ACCENT|| symbol == EXCLAMATION_MARK || symbol == QUESTION_MARK    ||
               symbol == APOSTROPHE       || symbol == DOT              || symbol == EQUAL            ||
               symbol == UNDERSCORE;
    }

    public static boolean IsControlCharacter(char control){
        return control == BACK_SPACE            || control == HORIZONTAL_TABULATION || control == LINE_FEED ||
               control == VERTICAL_TABULATION   || control == CARRIAGE_RETURN       || control == SPACE;
    }

    public static boolean IsLetter(char letter) {
        return letter >= 'a' && letter <= 'z' || letter >= 'A' && letter <= 'Z';
    }

    public static boolean IsHexa(char hexa) {
        return hexa >= 'A' && hexa <= 'F';
    }

    public static boolean IsDigit(char digit) {
        return digit >= '0' && digit <= '9';
    }

    public static boolean IsValidChar(char symbol) {
        return (IsLetter(symbol) || IsDigit(symbol) || IsSpecialCharacter(symbol) || IsControlCharacter(symbol) || symbol == QUOTATION_MARK);
    }
}