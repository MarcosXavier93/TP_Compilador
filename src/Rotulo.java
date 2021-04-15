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

public class Rotulo {
	static int contador;
	
	public Rotulo(){
		contador = 0;
	}
	
	public void resetRotulo(){
		contador = 0;
	}
	
	public String novoRotulo(){
		return "R" + contador++;
	}
}
