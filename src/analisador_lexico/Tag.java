package analisador_lexico;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tag {

	public final static int
		//Palavras reservadas
		INIT    = 256,
                STOP    = 257,
                IS      = 258,
                INT     = 259, //integer
                STR     = 260, //string
                REAL    = 261,
                IF      = 262,
                BEGIN   = 263,
		END     = 264,
		ELSE    = 265,
		DO 	= 266,
                WHILE   = 267,
                READ    = 268,
                WRITE   = 269,
                NOT     = 270,
              
		//Operadores e pontuação
		PV 	= (int)';',
		VRG     = (int)',',
                PPV     = 271, // := assign
		AP 	= (int)'(',
		FP	= (int)')',
                EQ      = (int)'=',
                GT	= (int)'>',
                GE      = 272, // >=
                LT	= (int)'<',
                LE      = 273, // <=
                GL      = 274, // <>
		NE      = 291, // !=
                SUM     = (int)'+',		
		MIN     = (int)'-',
                OR      = 275, // or
		MUL	= (int)'*',
		DIV     = (int)'/',
                AND     = 276, // and

		//Outros tokens
		NUM     = 300,
		ID      = 301,
		LIT     = 302,
                VOID    = 303,
		EOF     = 65535;

	public final static Set<Character> validASCIITokens = new HashSet<>(Arrays.asList(';', ',', '=', '(', ')', '-', '+', '*', '/'));

}