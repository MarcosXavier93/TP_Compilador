package analisador_lexico;

public class Word extends Token{

	private String lexeme = "";
	//public static final Word eq = new Word ("==", Tag.EQ, 0);
	public static final Word ge = new Word (">=", Tag.GE,0);
	public static final Word le = new Word ("<=", Tag.LE,0);
    public static final Word gl = new Word ("<>", Tag.GL,0);
    public static final Word ppv = new Word (":=", Tag.PPV,0);
	//public static final Word ne = new Word ("!=", Tag.NE,0);
	//public static final Word and = new Word ("&&", Tag.AND,0);
	//public static final Word or = new Word ("||", Tag.OR,0);

	
	public Word (String s, int tag, int line){
		super (tag, line);
		lexeme = s;
	}

	
	public String toString(){
		return "" + lexeme;
	}
	
	public String getLexeme(){
		return lexeme;
	}
}
