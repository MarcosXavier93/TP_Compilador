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
	/** Método construtor */
	public Lexer(String fileName) throws FileNotFoundException {

		try {
			file = new FileReader(fileName);
		} catch(FileNotFoundException e){
			System.out.println("Arquivo não encontrado");
			throw e;
		}

	
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
                int line_comentario = 0;
		//Desconsidera delimitadores na entrada
		for (;; readch()) {
			if (ch == (char)Tag.EOF) {
                            if (is_comentario)
				throw new InvalidTokenException("Error(" + line_comentario + "): comentário não fechado");
                            break;
			} else if (ch == '%') {
				is_comentario=!is_comentario;
                                line_comentario = line;

			} else if (ch == '\n') {
				line++; //conta linhas
                        } /*else if (ch == '*') {
				readch();
				// operador de multiplicacao
				return new Token(Tag.MUL, line);
			} else if(ch == '/'){
				readch();
				// operador de divisao
				return new Token(Tag.DIV, line);
			}*/ else if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b' || is_comentario)
				continue;
			else break;
		}


		switch(ch){
			//Operadores
			case '<':
				if (readch('='))
					return new Token(Word.le, line);
                                else if (readch('>'))
					return new Token(Word.gl, line);
				else
					return new Token('<', line);
			case '>':
				if (readch('='))
					return new Token(Word.ge, line);
				else
					return new Token('>', line);
                        case ':':
				if (readch('='))
					return new Token(Word.ppv, line);
				else
					throw new InvalidTokenException(line, ':');
                        case '*':
                                readch();
                                return new Token(Tag.MUL, line);
                        case '/':
                                readch();
                                return new Token(Tag.DIV, line);
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
		if (Token.isLetter(ch) || Token.isUnderscore(ch)){
			StringBuffer sb = new StringBuffer();
			do {
				sb.append(ch);
				readch(); 
			} while(Token.isLetterOrDigitOrUnderscore(ch));
			
			if (Token.isLetter(sb.charAt(0)) == true) {  // O identificador não será aceito se começar com "_".
				String s = sb.toString();
				Word w = words.get(s.toLowerCase());
				if (w != null) {
					Token T = new Token(w, line);        //palavra já existe na HashTable
					return T;
				}
				w = new Word (s, Tag.ID, line);
				words.put(s, w);
				return w;
			}else{
                            throw new InvalidTokenException(line, sb.charAt(0));
                        }
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

	public void adicionapalavras() {
		// Insere palavras reservadas na HashTable
		adiciona_palavra_reservada(new Word("init", Tag.INIT,0));
                adiciona_palavra_reservada(new Word("stop", Tag.STOP,0));
                adiciona_palavra_reservada(new Word("is", Tag.IS,0));
		adiciona_palavra_reservada(new Word("integer", Tag.INT,0));
		adiciona_palavra_reservada(new Word("string", Tag.STR,0));
                adiciona_palavra_reservada(new Word("real", Tag.REAL,0));
		adiciona_palavra_reservada(new Word("if", Tag.IF,0));
		adiciona_palavra_reservada(new Word("begin", Tag.BEGIN,0));
                adiciona_palavra_reservada(new Word("end", Tag.END,0));
		adiciona_palavra_reservada(new Word("else", Tag.ELSE,0));
		adiciona_palavra_reservada(new Word("do", Tag.DO,0));
		adiciona_palavra_reservada(new Word("while", Tag.WHILE,0));
		adiciona_palavra_reservada(new Word("read", Tag.READ,0));
		adiciona_palavra_reservada(new Word("write", Tag.WRITE, 0));
		adiciona_palavra_reservada(new Word("not", Tag.NOT, 0));
                adiciona_palavra_reservada(new Word("or", Tag.OR, 0));
                adiciona_palavra_reservada(new Word("and", Tag.AND, 0));
	}


	/* Imprime todas as entradas da tabela de símbolos */
	public void imprimirTabela() {
		System.out.println("\n\n\n**** Tabela de símbolos ****\nEntrada\t\t|\t\tMais info");
		for (Map.Entry<String, Word> entrada : words.entrySet()) {
			System.out.println(entrada.getKey());
		}
	}
	/** Método para inserir palavras reservadas na HashTable */
	private void adiciona_palavra_reservada(Word w) {
		words.put(w.getLexeme(), w); // lexema é a chave para entrada
	}

}