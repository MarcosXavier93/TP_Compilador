package analisador_lexico;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import exception.InvalidTokenException;

public class Lexer {

	public static int line = 1; 	//contador de linhas
	private char ch = ' '; 			//caractere lido do arquivo
	private FileReader file;
	private Hashtable<String, Word> words = new Hashtable<>();

	/** Método para inserir palavras reservadas na HashTable */
	private void reserve(Word w) {
		words.put(w.getLexeme(), w); // lexema é a chave para entrada na
	}


	/** Método construtor */
	public Lexer(String fileName) throws FileNotFoundException {
		try {
			file = new FileReader(fileName);
		} catch(FileNotFoundException e){
			System.out.println("Arquivo não encontrado");
			throw e;
		}

		// Insere palavras reservadas na HashTable
		reserve(new Word("program", Tag.PRG,0));
		reserve(new Word("end", Tag.END,0));
		reserve(new Word("int", Tag.INT,0));
		reserve(new Word("string", Tag.STR,0));
		reserve(new Word("if", Tag.IF,0));
		reserve(new Word("then", Tag.THEN,0));
		reserve(new Word("else", Tag.ELSE,0));
		reserve(new Word("do", Tag.DO,0));
		reserve(new Word("while", Tag.WHILE,0));
		reserve(new Word("scan", Tag.SCAN,0));
		reserve(new Word("print", Tag.PRINT,0));
	}


	/*Lê o próximo caractere do arquivo*/
	public void readch() throws IOException {
		ch = (char) file.read();
	}


	/* Lê o próximo caractere do arquivo e verifica se é igual a c*/
	private boolean readch(char c) throws IOException{
		readch();
		if (ch != c) return false;
		ch = ' ';
		return true;
	}


	public Token scan() throws IOException, InvalidTokenException{
		boolean is_comentario = false;
		//Desconsidera delimitadores na entrada
		for (;; readch()) {
			if (ch == (char)Tag.EOF) {
				if (is_comentario)
					throw new InvalidTokenException("Error(" + line + "): comentário não fechado");
				break;
			
			} else if (ch == '%') {
				is_comentario = !is_comentario;

			} else if (ch == '\n') {
				line++; //conta linhas

			} else if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b' || is_comentario)
				continue;
			else break;
		}


		switch(ch){
			//Operadores
			case '&':
				if (readch('&'))
					return new Token(Word.and, line);
				else
					throw new InvalidTokenException(line, '&');
			case '|':
				if (readch('|'))
					return new Token(Word.or, line);
				else
					throw new InvalidTokenException(line, '|');
			case '=':
				if (readch('='))
					return new Token(Word.eq, line);
				else
					return new Token('=', line);
			case '<':
				if (readch('='))
					return new Token(Word.le, line);
				else
					return new Token('<', line);
			case '>':
				if (readch('='))
					return new Token(Word.ge, line);
				else
					return new Token('>', line);
			case '!':
				if (readch('='))
					return new Token(Word.ne, line);
				else
					return new Token('!', line);
			case '*':
				return new Token('*', line);
		}

		//	Números
		if (Character.isDigit(ch)){
			int value=0;
			do{
				value = 10*value + Character.digit(ch,10);
				readch();
			}while(Character.isDigit(ch));
			return new Num(value, line);
		}

		// Literais
		if(ch == '\"'){
			StringBuffer sb = new StringBuffer();
			do{
				sb.append(ch);
				readch();
				if(ch == '\n' || ch == (char)Tag.EOF){
					throw new InvalidTokenException(line, '\"');
				}
			}while(ch != '\"');
			sb.append('\"');
			readch();
			String s = sb.toString();
			Word w = new Word(s, Tag.LIT, line);
			return w;
		}

		// Identificadores
		if (Token.isLetter(ch)){
			StringBuffer sb = new StringBuffer();
			do {
				sb.append(ch);
				readch();
			} while(Token.isLetterOrDigit(ch));
			String s = sb.toString();
			Word w = words.get(s);
			if (w != null) {
				Token T = new Token(w, line);        //palavra já existe na HashTable
				return T;
			}
			w = new Word (s, Tag.ID, line);
			words.put(s, w);
			return w;
		}

		// Caracteres ASCII validos
		if(Tag.validASCIITokens.contains(ch) || ch == (char)Tag.EOF){
			Token t = new Token(ch, line);
			ch = ' ';
			return t;
		}else{
			throw new InvalidTokenException(line, ch);
		}
	}

	/* Imprime todas as entradas da tabela de símbolos */
	public void imprimirTabela() {
		System.out.println("\n\n\n**** Tabela de símbolos ****\nEntrada\t\t|\t\tMais info");
		for (Map.Entry<String, Word> entrada: words.entrySet()) {
			System.out.println(entrada.getKey());
		}
	}

}