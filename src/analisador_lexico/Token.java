package analisador_lexico;

public class Token {

	public final int tag; //constante que representa o token
	public int line; //linha que o token apareceu
	private String lexeme = "1";

	public Token (int t, int line) {
		tag = t;
		this.line = line;
	}

	public Token (Word w, int line) {
		tag = w.tag;
		lexeme = w.getLexeme();
		this.line = line;
	}

	public String toString(){
		if(lexeme == "1") return "" + lexeme;
		else return "" + tag;
	}

	public String getLexeme(){
		return lexeme;
	}


	public void imprimeToken(Token T) {
		String valor;
		switch (tag) {
			case Tag.PRG:
				valor = "program";
				break;
			case Tag.END:
				valor = "end";
				break;
			case Tag.INT:
				valor = "int";
				break;
			case Tag.STR:
				valor = "string";
				break;
			case Tag.IF:
				valor = "if";
				break;
			case Tag.THEN:
				valor = "then";
				break;
			case Tag.ELSE:
				valor = "else";
				break;
			case Tag.DO:
				valor = "do";
				break;
			case Tag.WHILE:
				valor = "while";
				break;
			case Tag.SCAN:
				valor = "scan";
				break;
			case Tag.PRINT:
				valor = "print";
				break;
			case Tag.PV:
				valor = "ponto_virgula";
				break;
			case Tag.VRG:
				valor = "virgula";
				break;
			case Tag.SUM:
				valor = "soma";
				break;
			case Tag.ASS:
				valor = "assign";
				break;
			case Tag.AP:
				valor = "abre_parent";
				break;
			case Tag.FP:
				valor = "fecha_parent";
				break;
			case Tag.MIN:
				valor = "menos";
				break;
			case Tag.MUL:
				valor = "mult";
				break;
			case Tag.DIV:
				valor = "div";
				break;
			case Tag.GT:
				valor = "greater_than";
				break;
			case Tag.LT:
				valor = "less_than";
				break;
			case Tag.NOT:
				valor = "not";
				break;
			case Tag.EQ:
				valor = "equals";
				break;
			case Tag.GE:
				valor = "greater_equals";
				break;
			case Tag.LE:
				valor = "less_equals";
				break;
			case Tag.NE:
				valor = "not_equals";
				break;
			case Tag.AND:
				valor = "and";
				break;
			case Tag.OR:
				valor = "or";
				break;
			case Tag.NUM:
				valor = "num";
				break;
			case Tag.ID:
				valor = "identifier";
				break;
			case Tag.LIT:
				valor = "literal";
				break;
			default:
				valor = "" + (char)tag;
		}
		if (tag == Tag.LIT || tag == Tag.ID){
			System.out.println("< " + valor + ", " + T.getLexeme() + " >");
		}
		else if(tag == Tag.NUM){
			System.out.println("< " + valor + ", " + T + " >");

		} else {
			System.out.println("< " + valor + " >");
		}
	}

	public int getTag(){
		return tag;
	}

	public static boolean isLetter(char ch) {
		int A = (int)'A';
		int Z = (int)'Z';
		int a = (int)'a';
		int z = (int)'z';

		if (((int)ch >= A && (int)ch <= Z) || ((int)ch >= a && (int)ch <= z)) {
			return true;
		}
		return false;
	}


	public static boolean isLetterOrDigit(char ch) {
		int zero = (int) '0';
		int nove = (int) '9';
		if (isLetter(ch) || ((int) ch >= zero && (int) ch <= nove)) {
			return true;
		}
		return false;
	}
}