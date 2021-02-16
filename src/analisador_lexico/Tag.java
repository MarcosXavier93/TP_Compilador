package analisador_lexico;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tag {

	public final static int
		//Palavras reservadas
		PRG   = 256,
		END   = 257,
		INT   = 258,
		STR   = 259,
		IF    = 260,
		THEN  = 261,
		ELSE  = 262,
		DO 	  = 263,
		WHILE = 264,
		SCAN  = 265,
		PRINT = 266,
	
		//Operadores e pontuação
		PV 	= (int)';',
		VRG = (int)',',
		SUM = (int)'+',
		ASS = (int)'=',
		AP 	= (int)'(',
		FP	= (int)')',
		MIN = (int)'-',
		MUL	= (int)'*',
		DIV = (int)'/',
		GT	= (int)'>',
		LT	= (int)'<',
		NOT	= (int)'!',

		EQ  = 288, // ==
		GE  = 289, // >=
		LE  = 290, // <=
		NE  = 291, // !=
		AND = 292, // &&
		OR  = 293, // ||

		//Outros tokens
		NUM   = 300,
		ID    = 301,
		LIT = 302,
		VOID = 303,
		IS = 304,
		STOP = 305,
		REAL = 306,
		END_ELSE = 307,
		
		
		EOF = 65535;



	public final static Set<Character> validASCIITokens = new HashSet<>(Arrays.asList(';', ',', '=', '(', ')', '-', '+', '*', '/'));

}