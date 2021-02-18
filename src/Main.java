/*
Na etapa 1, o compilador deverá exibir a sequência de tokens identificados e os símbolos (identificadores e
palavras reservadas) instalados na Tabela de Símbolos. Nas etapas seguintes, isso não deverá ser exibido.
 */
//Commit para reparar possíveis bugs//

import analisador_lexico.Lexer;
import analisador_lexico.Tag;
import analisador_lexico.Token;
import exception.InvalidTokenException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {


	public static void main(String[] args) {
		ArrayList<Token> tokens = new ArrayList<Token> ();
		Lexer L = null;
		int line = -5;
		try {
			//L = new Lexer("D:\\- Particular\\Estudos\\Cefet\\OneDrive - aluno.cefetmg.br\\2020.2\\compil\\trab\\TP_Compilador\\src\\codigos_teste\\Teste1.txt");
			L = new Lexer("codigos_teste/Teste2.txt");
			
			L.adicionapalavras();//Inicia adicionando palavras reservadas
			System.out.println("**** Tokens lidos ****");
			// Apenas para entrar no laço
			Token T = new Token(0, line);
			while (T.tag != Tag.EOF) {
				try {
					T = L.scan();
					if(T.tag == Tag.EOF)
						break;
					T.imprimeToken(T);
					tokens.add(T);
					line = T.line;
				} catch (InvalidTokenException | IOException e) {
					System.out.println(e.getMessage());
					try {
						L.readch();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			line++;
			tokens.add(new Token(Tag.EOF, line));
			L.imprimirTabela();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
