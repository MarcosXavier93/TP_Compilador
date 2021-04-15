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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class Buffer {

    public ArrayList<String> buffer;
    private String _fileName;

    public Buffer(String file) {
        buffer = new ArrayList<>();
        _fileName = file;
    }

    public void print() {
        try {
            BufferedWriter _file = new BufferedWriter(new FileWriter(_fileName));

            for (String s : buffer) {
                _file.write(s);
                _file.newLine();
            }

            _file.close();

        } catch(Exception e) {}
    }

    public void add(String str) {
        buffer.add(str);
    }
}